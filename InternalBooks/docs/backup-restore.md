## MySQL バックアップ & リストア手順（EC2 + Docker + S3）

このドキュメントでは、EC2 上で動作する Docker コンテナ MySQL のバックアップと復元手順をまとめます。

---

## 1. バックアップ方針

1. **EBS スナップショット**
   - EC2 の MySQL データが格納された EBS ボリューム（例: `/data/mysql`）に対して、日次で自動スナップショットを取得。
   - インフラレベルのバックアップとして、ディスクごと復元したいケースに利用。

2. **論理バックアップ (mysqldump → S3)**
   - `mysqldump` でデータベースを SQL としてダンプし、gzip 圧縮して S3 バケットに保存。
   - テーブル単位の復元や、個別時点のデータ参照に便利。

両者を組み合わせることで、障害時の復旧手段を多層化します。

---

## 2. mysqldump → S3 スクリプト

本リポジトリの `scripts/mysql_to_s3.sh` は、MySQL コンテナから `mysqldump` を実行し、S3 へアップロードするサンプルスクリプトです。

### 2.1 前提

- EC2 インスタンスロールが、以下のような S3 アクセス権限を持っていること:
  - `s3:PutObject`, `s3:GetObject`, `s3:ListBucket` など
- `docker-compose.prod.yml` で MySQL コンテナ名が `internalbooks-mysql` であること
- AWS CLI がインストール済みであること

### 2.2 設置と権限

1. スクリプトを配置:

   - 例: `/opt/internalbooks/scripts/mysql_to_s3.sh`

2. 実行権限を付与:

```bash
chmod +x /opt/internalbooks/scripts/mysql_to_s3.sh
```

3. 環境変数（必要に応じて）:

- `.env.prod` またはシェル環境で、以下を上書き可能:
  - `MYSQL_CONTAINER_NAME`
  - `MYSQL_USER`
  - `MYSQL_PASSWORD`
  - `MYSQL_DATABASE`

---

## 3. cron による定期実行

EC2 内の `cron` を用いて、日次または数時間おきにバックアップを自動実行します。

### 3.1 cron の設定例（毎日 3:00 に実行）

```bash
crontab -e
```

以下を追加:

```cron
0 3 * * * /opt/internalbooks/scripts/mysql_to_s3.sh >> /var/log/mysql_backup.log 2>&1
```

- ログは `/var/log/mysql_backup.log` に出力されます。

---

## 4. EBS スナップショット

- MySQL データ用 EBS ボリュームに対して、AWS マネジメントコンソールまたは Amazon Data Lifecycle Manager を利用して日次スナップショットを設定します。
- 例:
  - 毎日 1 回スナップショットを取得
  - 7〜30 日分を保持

これにより、ディスク全体の状態を指定時点に戻すことができます。

---

## 5. 復元手順（概要）

### 5.1 mysqldump からの復元

1. 復元対象のバックアップファイルを S3 から取得:

```bash
aws s3 cp s3://internalbooks-prod-mysql-backup/mysql/internalbooks-YYYYMMDD-HHMMSS.sql.gz /opt/backup/
cd /opt/backup
gunzip internalbooks-YYYYMMDD-HHMMSS.sql.gz
```

2. 対象データベースに流し込み:

```bash
docker exec -i internalbooks-mysql \
  sh -c "mysql -u\"${MYSQL_USER}\" -p\"${MYSQL_PASSWORD}\" \"${MYSQL_DATABASE}\"" \
  < internalbooks-YYYYMMDD-HHMMSS.sql
```

### 5.2 EBS スナップショットからの復元（高レベル概要）

1. 対象スナップショットから新しい EBS ボリュームを作成
2. EC2 にアタッチ
3. 必要に応じて、現行の `/data/mysql` を退避し、新しいボリュームを `/data/mysql` にマウント
4. Docker（MySQL コンテナ）を再起動

※ 実運用に入る前に、別環境でこの手順をリハーサルしておくことを推奨します。

---

## 6. テストのすすめ

- バックアップは「**復元できて初めて意味がある**」ため、以下を定期的に実施してください。
  - S3 に保存されたバックアップファイルから、検証用環境での復元テスト
  - EBS スナップショットからの復元シナリオ確認

