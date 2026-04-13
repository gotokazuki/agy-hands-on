---
trigger: always_on
---

## 開発ワークフロー

API の定義、データベーススキーマ、および実装の順序に関する厳格なルールを定義する。

- Contract-First の徹底
  - `docs/openapi.yaml` を「唯一の正解（Single Source of Truth）」とする。
  - 実装を開始する前に、必ず OAS の定義内容を確認し、これから作成する機能のスコープについて要約を提示し、ユーザーと合意形成を行うこと。
  - 既に定義が完了している場合は、定義内容を正しく読み取ったことをユーザーに示し、実装開始の承諾を得ること。

- 実装の連鎖フロー
  - 1. `docs/openapi.yaml` および `backend/src/main/resources/db/migration/` 内の既存ファイルを確認し、プロジェクトの全体構造を把握する。
  - 2. `docker compose up -d postgres` を実行し、データベースを起動する。
  - 3. `./gradlew flywayMigrate` を実行し、準備済みの SQL をデータベースに反映する。
  - 4. backend で `./gradlew jooqGenerate` を実行し、テーブル定義から Java コードを生成する。
  - 5. frontend で Orval を実行し、OAS から Hooks を生成する。
  - 6. すべての自動生成が成功し、型定義が最新になったことを確認してから、ビジネスロジックの実装に着手する。

- 検証プロセス
  - 実装完了後は Antigravity Browser (Chrome環境) を使用し、実際の画面で挙動を確認すること。
  - 特に、ブラウザ固有の挙動（CORS、Cookie、JWTの送信、リダイレクト等）に問題がないかを必ず Chrome で検証すること。
  - 既存の SQL で投入された初期データ（管理者ユーザー等）を使用してログイン・操作ができるかを必ず実機（ブラウザ）で検証すること。
  - 視覚的なレイアウト崩れやコンソールエラーがないかを目視（画像解析）で確認すること。
