#!/usr/bin/env bash
set -euo pipefail

# MySQL → S3 バックアップスクリプト
# 前提:
# - EC2 インスタンスロールに S3 への書き込み権限があること
# - docker-compose の MySQL コンテナ名が internalbooks-mysql であること
# - aws CLI がインストール・設定済みであること（ロール経由）

BACKUP_DIR="/opt/backup"
TIMESTAMP="$(date '+%Y%m%d-%H%M%S')"
S3_BUCKET="internalbooks-prod-mysql-backup"
S3_PREFIX="mysql"

# 必要に応じて環境変数から上書き可能
MYSQL_CONTAINER_NAME="${MYSQL_CONTAINER_NAME:-internalbooks-mysql}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-${MYSQL_ROOT_PASSWORD:-}}"
MYSQL_DATABASE="${MYSQL_DATABASE:-internalbooks}"

mkdir -p "${BACKUP_DIR}"

BACKUP_FILE="${BACKUP_DIR}/${MYSQL_DATABASE}-${TIMESTAMP}.sql.gz"

echo "Starting MySQL backup: container=${MYSQL_CONTAINER_NAME}, db=${MYSQL_DATABASE}"

docker exec "${MYSQL_CONTAINER_NAME}" \
  sh -c "mysqldump -u\"${MYSQL_USER}\" -p\"${MYSQL_PASSWORD}\" \"${MYSQL_DATABASE}\"" \
  | gzip > "${BACKUP_FILE}"

echo "Uploading to S3: s3://${S3_BUCKET}/${S3_PREFIX}/$(basename "${BACKUP_FILE}")"

aws s3 cp "${BACKUP_FILE}" "s3://${S3_BUCKET}/${S3_PREFIX}/"

echo "Backup completed: ${BACKUP_FILE}"

