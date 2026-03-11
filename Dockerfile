# アプリケーション本番用 Dockerfile
# ビルド: docker build -t internalbooks-app:1.0.0 .
# リポジトリ直下で実行すること

FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle プロジェクトをコピー
COPY InternalBooks/ .

# JAR をビルド（テストはスキップして高速化、本番ビルド時は -x test を外すことを推奨）
RUN ./gradlew bootJar --no-daemon -x test

# 実行用ステージ
FROM eclipse-temurin:21-jre
WORKDIR /app

# ヘルスチェック用に curl をインストール
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# ビルド成果物をコピー
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 443 80
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
