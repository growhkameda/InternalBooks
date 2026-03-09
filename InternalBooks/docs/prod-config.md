## InternalBooks 本番(prod)設定ガイド

このドキュメントは、AWS EC2 上で Docker コンテナとして InternalBooks を本番運用する際の設定方針をまとめたものです。

---

## 1. Spring Boot のプロファイル構成

- プロファイル:
  - 開発: `dev`（既存）
  - 本番: `prod`（新規）
- 本番起動時は、環境変数でプロファイルを指定します。

```bash
SPRING_PROFILES_ACTIVE=prod
```

- Docker 本番用構成では、`.env.prod` に以下のように記載します。

```env
SPRING_PROFILES_ACTIVE=prod
```

---

## 2. application-prod.properties の例

アプリケーション本体のリポジトリ側で、`src/main/resources/application-prod.properties` を作成し、以下のような内容をベースに調整します。

```properties
# サーバ設定
server.port=${SERVER_PORT:8080}

# ログレベル
logging.level.root=INFO
logging.level.org.springframework.web=INFO

# データソース設定（コンテナ内 MySQL を利用）
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://${DB_HOST:mysql}:${DB_PORT:3306}/${DB_NAME:internalbooks}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Tokyo
spring.datasource.username=${DB_USER:internalbooks}
spring.datasource.password=${DB_PASSWORD:change_me_app}

# JPA / Hibernate（利用している場合）
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# その他、本番用の設定を必要に応じて追加
```

- 上記では、`DB_HOST` などの値を **環境変数から取得** し、デフォルト値も設定しています。
- Docker 環境では、`.env.prod` で値を定義し、コンテナ起動時に引き渡します。

---

## 3. Docker 本番用構成との連携

- 本リポジトリ直下に用意した `docker-compose.prod.yml` では、以下のような前提で構成しています。
  - アプリコンテナ: `internalbooks-app:1.0.0`（ビルド済みイメージを想定）
  - MySQL コンテナ: `mysql:8.0`
  - MySQL データディレクトリ: `/var/lib/mysql` を EC2 上の `/data/mysql`（EBSマウント先）にバインド
  - 環境変数は `.env.prod` から読み込み

- `.env.prod` は、`.env.prod.example` をコピーして作成します。

```bash
cp .env.prod.example .env.prod
vi .env.prod # 各値を本番用に変更
```

---

## 4. .env.prod の配置と管理

- `.env.prod` ファイルは **Git にコミットしない** ように `.gitignore` で除外してください。
- EC2 上では、アプリ配置ディレクトリ（例: `/opt/internalbooks`）直下に `.env.prod` を配置し、`docker-compose.prod.yml` と同じディレクトリで管理する想定です。

---

## 5. ローカルでのprod動作確認

本番に近い設定でローカル検証をする場合:

1. `.env.prod.example` をコピーしてローカル用 `.env.prod` を作成
2. `DB_HOST=mysql` のままにしておき、MySQL コンテナを利用
3. 以下コマンドで起動

```bash
docker-compose -f docker-compose.prod.yml up -d
```

4. ブラウザから `http://localhost/` にアクセスして動作確認

---

このドキュメントは、本番用設定の方針をまとめたものです。実際の `application-prod.properties` の編集は、アプリケーション本体のリポジトリ側で行ってください。

