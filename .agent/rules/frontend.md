---
trigger: glob
globs: frontend/**/*
---

## フロントエンド実装ルール

### 技術スタック

エージェントは以下のライブラリを前提に実装を行うこと。

- React (Vite) / TypeScript: 開発基盤
- TanStack Router: File-based routing による型安全なルーティング
- TanStack Query: 非同期データフェッチおよびサーバー状態管理
- Zustand: クライアント側のグローバル状態管理
- React Hook Form / Zod: フォーム管理およびバリデーション
- TailwindCSS / shadcn/ui: スタイリングおよび UI コンポーネント基盤
- Orval: OpenAPI Specification からのコード自動生成（Hooks, Models, Zod schemas）

### ディレクトリ構造

エージェントは以下の構造を厳守し、ファイルの新規作成や変更を行うこと。

```text
- src/
  - api/                              # API連携の基盤
    - generated/                      # Orval生成ファイル（hooks, models, zod）
  - assets/                           # 静的リソース
    - images/                         # 写真、イラスト
    - icons/                          # SVGアイコンなど
    - styles/                         # グローバルCSS（Tailwind以外のベース設定）
  - components/                       # 汎用的な共通部品
    - ui/                             # shadcn/ui（基本手動で触らない）
    - common/                         # 自作の汎用コンポーネント（Button, Input等）
    - layouts/                        # 構造的な部品（Stack, Grid, Container等）
  - features/                         # 【最重要】ドメインごとのカプセル化
    - {feature-name}/                 # 例: auth, users, groups
      - api/                          # Orval生成Hookをラップする機能専用Hook
      - assets/                       # その機能だけで使う画像
      - components/                   # その機能だけで使うUI部品
      - hooks/                        # その機能の複雑なロジック（Composables）
      - stores/                       # その機能の状態管理（Zustand等）
      - types/                        # その機能特有の型定義
      - utils/                        # その機能特有のヘルパー関数
  - hooks/                            # アプリ全体で使う汎用カスタムフック（useDebounce等）
  - layouts/                          # ページ全体の枠組み（MainLayout, AuthLayout等）
  - lib/                              # 外部ライブラリの設定（axios, dayjs等の初期化）
  - routes/                           # TanStack Router のルート定義
    - users/                          # /users 関連
      - $userId.tsx                   # /users/123
      - index.tsx                     # /users
    - groups/                         # /groups 関連
      - $groupId.tsx                  # /groups/123
      - index.tsx                     # /groups
    - __root.tsx                      # 全ルートのベース（DevTools等）
  - services/                         # APIクライアントの設定や認証トークン管理
  - store/                            # アプリ全体のグローバル状態（UserSession等）
  - types/                            # プロジェクト全体の共通型定義
  - utils/                            # 汎用ユーティリティ（date, validation等）
  - App.tsx                           # Provider群のラップ
  - main.tsx                          # エントリポイント
```

### 実装ガイドライン（遵守事項）

エージェントは実装時、以下の制約を常に考慮すること。

- コンポーネント設計とデザインの一貫性
  - UI パーツには必ず shadcn/ui を優先使用すること。独自実装を最小限に抑え、既存のコンポーネントを組み合わせて構築すること。
  - スタイリングは Tailwind CSS を使用し、デザインの一貫性を保つこと。
  - 複数の機能で横断的に利用されるパーツは `src/components/common/` に、特定のドメインに閉じるパーツは `src/features/{feature}/components/` に配置し、カプセル化を維持すること。

- Orval による自動生成と型安全
  - API クライアントコードを手動で実装しないこと。`src/api/generated/` に自動生成されたコードのみを利用する。
  - 型定義の優先順位は、OAS からの自動生成を「正」とする。
  - 手動での型定義は、UI 固有の状態管理や、API に依存しない純粋なドメインモデルが必要な場合に限定すること。

- フォーム実装の厳格化
  - フォーム実装には React Hook Form を使用すること。
  - バリデーションには Zod を使用すること。Orval によって `src/api/generated/` に生成された Zod スキーマをベースにし、UI 固有の制約がある場合のみ拡張すること。
  - これにより、OAS の定義とフロントエンドのバリデーションの不整合を排除すること。

- ルーティングとアクセス制御
  - TanStack Router の File-based routing を採用し、`beforeLoad` を用いてルートレベルの認可制御を行ってください。
  - 権限のないページにアクセスしようとした場合は、そのユーザーがアクセス可能な適切なページ（例: `/groups`）へリダイレクトしてください。
  - ルート定義ファイル（`$userId.tsx` 等）は「ページの入り口」に徹し、肥大なロジックを書かないこと。実際のコンテンツは `features/` 配下のコンポーネントを呼び出す形式をとること。

- 認可ベースの UI 制御
  - `src/lib/auth-utils.ts` の `hasAuthority` 関数を使用して、権限に基づく UI の表示・非表示、およびボタンの活性化・非活性化を制御してください。
  - サイドバー等のナビゲーション要素は、ユーザーの権限に応じて動的にフィルタリングされるように実装してください。

- API 通信と認証トークン
  - `src/shared/api/axios.ts` に定義された Axios インスタンスを「正」とし、すべての API 通信でこれを使用してください。
  - Axios インターセプターを使用して、すべてのリクエストに `Authorization: Bearer <token>` を自動的に付与してください。
  - 401 Unauthorized エラーを検知した場合、リフレッシュトークンを使用してトークンの再取得を試みる機能をインターセプターに含めてください。トークン再取得に失敗した場合は、ログイン画面へリダイレクトしてください。

- エラーハンドリングとユーザー通知
  - バックエンドからの RFC 7807 形式のエラーレスポンスを受け取った際は、`useToast` (shadcn/ui) を使用してエラーメッセージをユーザーに視覚的に通知してください。
  - エラーの詳細は `error.response.data.detail` または `title` から取得してください。

- フォーム実装とバリデーション
  - フォーム実装には React Hook Form を使用し、バリデーションには Orval 経由で生成された Zod スキーマを優先して適用してください。
  - 特定のフィールドのみを更新する PATCH リクエストを投げる場合は、フォームの値をオプショナルにするなどの工夫を行い、バックエンドの期待する型と一致させてください。

- パスエイリアスと命名規則
  - インポート時は常に `@/` エイリアス（src ディレクトリを指す）を使用すること。
  - ディレクトリ名は kebab-case、React コンポーネント名は PascalCase、ファイル名は役割（コンポーネントなら PascalCase, それ以外は kebab-case）を遵守すること。