# 使用するベースイメージを指定
FROM mysql:8.4

# 環境変数の設定
ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=internalbooks

# my.cnf を動的に生成
RUN echo "[mysqld]" > /etc/mysql/my.cnf && \
    echo "character-set-server = utf8mb4" >> /etc/mysql/my.cnf && \
    echo "collation-server = utf8mb4_unicode_ci" >> /etc/mysql/my.cnf && \
    echo "max_connections = 100" >> /etc/mysql/my.cnf && \
    echo "init_connect = 'SET NAMES utf8mb4; SET CHARACTER SET utf8mb4;'" >> /etc/mysql/my.cnf && \
    echo "sql_mode = STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION" >> /etc/mysql/my.cnf && \
    echo "[client]" >> /etc/mysql/my.cnf && \
    echo "default-character-set = utf8mb4" >> /etc/mysql/my.cnf && \
    echo "[mysql]" >> /etc/mysql/my.cnf && \
    echo "default-character-set = utf8mb4" >> /etc/mysql/my.cnf

# MySQLコンテナの起動
CMD ["--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci", "--init-connect='SET NAMES utf8mb4;'"]