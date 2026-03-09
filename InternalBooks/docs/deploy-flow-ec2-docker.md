## デプロイフロー（EC2 + Docker + docker-compose.prod.yml）

このドキュメントでは、InternalBooks を EC2 上で Docker コンテナとして本番デプロイする手順（手動版）をまとめます。

---

## 1. 前提

- アプリケーションは Docker イメージとしてビルド可能
- 本番用の `docker-compose.prod.yml` と `.env.prod` が存在する（このリポジトリ直下）
- EC2 インスタンス:
  - Docker / docker-compose（または `docker compose`）がインストール済み
  - 必要に応じて Git もインストールし、本リポジトリをクローンしておく

---

## 2. Docker イメージのビルド & プッシュ

### 2.1 ローカルマシンまたはCIでビルド

1. アプリケーションの Docker イメージをビルド:

```bash
docker build -t internalbooks-app:1.0.0 .
```

2. レジストリへプッシュ（例: Amazon ECR を利用する場合）:

```bash
AWS_ACCOUNT_ID=xxxxxxxxxxxx
AWS_REGION=ap-northeast-1
REPO_NAME=internalbooks-app

aws ecr get-login-password --region ${AWS_REGION} \
  | docker login \
    --username AWS \
    --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

docker tag internalbooks-app:1.0.0 ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:1.0.0
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${REPO_NAME}:1.0.0
```

3. `docker-compose.prod.yml` の `image:` を、ECR 上のフルパスに変更:

```yaml
services:
  app:
    image: xxxxxxxxxxxx.dkr.ecr.ap-northeast-1.amazonaws.com/internalbooks-app:1.0.0
    # ...
```

※ 初期は Docker Hub のプライベートリポジトリを利用することも可能です。

---

## 3. EC2 上の初期セットアップ

### 3.1 必要パッケージのインストール

```bash
sudo yum update -y
sudo yum install -y docker git

sudo systemctl enable docker
sudo systemctl start docker
```

必要に応じて docker-compose（v1）または docker compose（v2）を導入します。

### 3.2 リポジトリの配置

例として、`/opt/internalbooks` に配置する場合:

```bash
sudo mkdir -p /opt/internalbooks
sudo chown ec2-user:ec2-user /opt/internalbooks

cd /opt/internalbooks
git clone <このリポジトリのURL> .
```

### 3.3 EBS ボリュームのマウント

1. EBS ボリュームを EC2 にアタッチ
2. ファイルシステム作成（初回のみ）:

```bash
sudo mkfs -t xfs /dev/nvme1n1   # デバイス名は環境に応じて変更
```

3. マウントポイント作成 & マウント:

```bash
sudo mkdir -p /data/mysql
sudo mount /dev/nvme1n1 /data/mysql
```

4. `/etc/fstab` に追記して自動マウント設定（省略）

---

## 4. .env.prod の作成

```bash
cd /opt/internalbooks
cp .env.prod.example .env.prod
vi .env.prod  # パスワード等を本番用に修正
```

---

## 5. 本番コンテナの起動・更新手順

### 5.1 初回起動

```bash
cd /opt/internalbooks
docker compose -f docker-compose.prod.yml pull   # app イメージをレジストリから取得
docker compose -f docker-compose.prod.yml up -d
```

※ `docker-compose` コマンド利用の場合は `docker-compose -f ...` に読み替え。

### 5.2 新バージョンへの更新

1. 新しいタグで Docker イメージをビルド＆プッシュ（例: `1.0.1`）
2. `docker-compose.prod.yml` の `image:` を新タグに変更
3. EC2 上で:

```bash
cd /opt/internalbooks
git pull origin main   # または使用中のブランチ
docker compose -f docker-compose.prod.yml pull
docker compose -f docker-compose.prod.yml up -d
```

---

## 6. ロールバック戦略（簡易版）

- 直前の安定版タグ（例: `1.0.0`）のイメージを保持しておき、`docker-compose.prod.yml` の `image:` を戻して再度 `up -d` する。
- DB に破壊的な変更がある場合は、ロールバック前にバックアップと整合性確認が必要。

---

## 7. 手動運用から自動化への発展

- 本ドキュメントの手順に慣れたら、以下のような自動化も検討できます。
  - GitHub Actions から EC2 へ SSH 接続し、`git pull` と `docker compose up -d` を自動実行
  - CodeDeploy / CodePipeline を利用したより本格的なデプロイフロー

