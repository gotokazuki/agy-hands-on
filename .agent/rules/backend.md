---
trigger: glob
globs: backend/**/*
---

## バックエンド実装ルール

### 技術スタック

エージェントは以下の環境およびライブラリを前提に実装を行うこと。

- Java 25 / Spring Boot 4.0: 実行基盤
- Gradle (Kotlin DSL): ビルドツール
- Spring Security: 認証・認可フレームワーク
- Spring Boot Starter jOOQ: データベース操作（型安全なクエリ生成）
- Flyway: データベースマイグレーション
- JUnit 5 / MockMVC / Testcontainers: テストフレームワーク
- jjwt: JWT ライブラリ

### ディレクトリ構造 (Clean Architecture)

エージェントは以下の構造を厳守し、パッケージおよびクラスを作成すること。

```text
- src/main/java/com/example/agyhandson/
  - domain/             # ドメイン層：純粋なビジネスロジック
    - model/            # ドメインエンティティ、値オブジェクト
    - repository/       # リポジトリのインターフェース（Interfaceのみ）
    - service/          # ドメインサービス（複数のエンティティに跨るロジック）
  - usecase/            # ユースケース層：アプリケーション固有のルール
    - input/            # 入力ポート・モデル (LoginCommand など)
    - output/           # 出力ポート・モデル (UserResultDTO など)
    - service/          # ユースケースの実装（Interactor）
  - infrastructure/     # インフラストラクチャ層：外部フレームワーク依存の実装
    - persistence/      # 永続化の実装
      - jooq/           # jOOQ 固有の Repository 実装と Converter
    - external/         # 外部システム（API、メール送信等）との連携実装
  - presenter/          # プレゼンテーション層：外部インターフェース
    - controller/       # REST Controller
    - request/          # リクエスト DTO（OAS の定義と一致させる）
    - response/         # レスポンス DTO（OAS の定義と一致させる）
  - config/             # コンフィギュレーション：DI 設定、Bean 定義、Security 設定
```

### 実装ガイドライン

- データベース操作（jOOQ / Flyway）
  - SQL マイグレーションファイルは `backend/src/main/resources/db/migration/` に配置すること。
  - ソースコード内での直接的な SQL 文字列（Raw SQL）の記述は禁止する。必ず自動生成された jOOQ の型安全なクエリを使用すること。
  - UUID は `java.util.UUID` を使用し、データベース側でのデフォルト値は `gen_random_uuid()` を利用すること。

- 認可
  - JWT の Claims に含まれる `permissions` 配列（例: `user:read`, `user:write`, `group:read`, `group:write`, `permission:read`, `permission:write`）を読み取り、Spring Security の `GrantedAuthority` にマッピングしてください。
  - 各 Controller または UseCase のメソッドに `@PreAuthorize` を付与し、パーミッションベースの認可を実装してください。具体的には `.agent/rules/auth_mapping.md` を参照してください。
  - リソースの更新において、特定のフィールド（権限の割り当て等）の変更に特別な権限が必要な場合は、Controller 内で明示的な権限チェック（`SecurityContextHolder` を利用）を行ってください。

- 更新操作 (PATCH)
  - リソースの部分更新を行う場合は、`PUT` ではなく `PATCH` メソッドを使用してください。
  - 実装時は、既存のエンティティを取得し、リクエストで `null` ではないフィールドのみを上書きする「既存値の維持」パターンを徹底してください。
  - 値が送信されなかった場合（null）と、明示的に null が送信された場合を区別する必要がある場合は `Optional` 等を検討してください。

- トークン管理
  - アクセストークンに加えて、リフレッシュトークン（`refresh_token`）によるセッション維持を実装しています。
  - `auth/refresh` エンドポイントが提供されていることを確認し、トークンの有効期限管理を厳格に行ってください。

- トランザクション管理
  - データの作成、更新、削除を伴うユースケース・サービスメソッドには必ず `@Transactional` を付与し、不整合が発生しないようにしてください。

- エラーハンドリング
  - API エラーは RFC 7807 (Problem Details for HTTP APIs) に準拠したレスポンスを返してください。
  - `GlobalExceptionHandler` で共通的なエラー変換ロジックを維持してください。

- 命名規則とマッピング
  - バックエンドの DTO（Request/Response）名は、`docs/openapi.yaml` の `schemas` セクションで定義された名称と完全に一致させること。
  - レイヤー間のオブジェクト変換（Entity から DTO など）が必要な場合は、明示的なコンバーターまたはマッパーを作成すること。

### テスト方針

- テストの構成
  - 単体テストには JUnit 5 を使用すること。
  - API エンドポイントのテストには MockMVC を使用すること。
  - 認可のテストでは、適切な Authority を持たせたモックユーザーを使用してテストケースを網羅すること。
  - データベースや外部サービスが絡む統合テストには Testcontainers を使用し、モックではなく本物のコンテナ環境に対してテストを実行すること。
  - テストコード内でも jOOQ を使用し、型安全にアサーションを行うこと。