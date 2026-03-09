## AWS 最小構成 (EC2 + Docker + MySQL + EBS + S3)

このドキュメントは、InternalBooks を AWS 上で **RDS を使わず**、EC2 上の Docker コンテナ＋MySQL＋EBS＋S3 で本番運用するための最小構成を定義します。

---

## 1. 全体構成概要

- VPC: 1つ（CIDR 例: `10.0.0.0/16`）
- サブネット:
  - パブリックサブネット 1つ（例: `10.0.1.0/24`）
- インターネットゲートウェイ: 1つ
- EC2:
  - 1台（アプリコンテナ＋MySQLコンテナを同居）
- EBS:
  - ルートボリューム（OS 用）
  - データボリューム（MySQL 用、/data/mysql にマウント）
- S3:
  - 1バケット（mysqldump バックアップ保存用）

---

## 2. EC2 設計

- インスタンスタイプ（初期案・コスト重視）:
  - `t3.small` または `t4g.small`（Arm ベース。Java が Arm 対応なら t4g 系も候補）
- OS:
  - Amazon Linux 2023 などの最新世代
- ストレージ:
  - ルートボリューム: 20〜30GB, gp3
  - データ用 EBS ボリューム: 20〜50GB, gp3（後から拡張可能）
- ネットワーク:
  - パブリックサブネットに配置
  - パブリック IP を付与（ALB を使わないシンプル構成を想定）

---

## 3. EBS（MySQL データ用）設計

- ボリューム:
  - タイプ: gp3
  - サイズ: 20〜50GB（利用状況を見て拡張）
  - AZ: EC2 と同じ AZ に作成
- マウント:
  - EC2 上で `/data/mysql` にマウント
  - `fstab` に登録して再起動後も自動マウント
- Docker:
  - `docker-compose.prod.yml` で `/data/mysql:/var/lib/mysql` としてマウントし、MySQL コンテナのデータを永続化

---

## 4. S3 バケット設計

- バケット名（例）:
  - `internalbooks-prod-mysql-backup`
- 設定:
  - バージョニング: 有効化
  - 暗号化: デフォルト SSE-S3 を有効化
  - ライフサイクルルール（任意）:
    - 例: 90日後に削除、または Glacier に移行

---

## 5. セキュリティグループ設計

### 5.1 Web 用 SG（例: `sg-internalbooks-web`）

- Inbound:
  - HTTP (80): クライアントのアクセス元 IP に応じて制限
    - 初期は `0.0.0.0/0` でもよいが、将来的には ALB 配下や社内 IP のみなどに制限
  - HTTPS (443): 将来 TLS 終端を行う場合に利用（当面は 80 のみでも可）
  - SSH (22): 自宅/オフィスの固定 IP のみ許可
- Outbound:
  - デフォルトの `0.0.0.0/0` 許可で問題なし（OS/パッケージ更新、S3 アクセス等に利用）

### 5.2 MySQL ポート

- 原則、**外部には公開しない**。
- MySQL コンテナは EC2 内部ネットワークでのみ利用し、3306 ポートは SG で閉じたままにする。
- デバッグ等で一時的に接続したい場合は、SSH ポートフォワーディングや、一時的な SG ルール開放で対応。

---

## 6. IAM ロール設計

- EC2 インスタンスロール（例: `InternalBooksEc2Role`）に付与するポリシー:
  - S3 バケットへの read/write 権限
    - 例: `arn:aws:s3:::internalbooks-prod-mysql-backup` 配下の `s3:GetObject`, `s3:PutObject`, `s3:ListBucket`
  - 将来、SSM Parameter Store / Secrets Manager を利用する場合の権限（必要時に追加）

- ポイント:
  - 可能な限り **アクセスキーの直書きは避ける**。
  - EC2 ロール経由で S3 等にアクセスする。

---

## 7. 運用上のメモ

- まずは上記の最小構成でスタートし、負荷や利用状況に応じて以下を検討:
  - EC2 のスケールアップ（t3.small → t3.medium など）
  - ALB の導入（将来 HTTPS 終端や複数インスタンス運用を行う場合）
  - RDS / Aurora への移行（可用性・運用コストとのトレードオフを見て判断）

