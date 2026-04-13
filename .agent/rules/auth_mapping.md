---
trigger: always_on
---

## 権限と操作のマッピング定義

現在の実装における、システム権限（Permissions）と提供される機能・アクセスの対応表です。

### 権限一覧と機能対応

| 権限名             | リソース     | アクション | 許可される主な操作                                                           | UI上の制限                                           |
| :----------------- | :----------- | :--------- | :--------------------------------------------------------------------------- | :--------------------------------------------------- |
| `user:read`        | `user`       | `read`     | ユーザー一覧の取得、ユーザー詳細の取得、システム統計（ダッシュボード）の取得 | サイドバーの「ダッシュボード」「ユーザー管理」の表示 |
| `user:write`       | `user`       | `write`    | ユーザーの新規作成、更新（PATCH）、削除                                      | ユーザー一覧における各種操作ボタンの有効化           |
| `group:read`       | `group`      | `read`     | グループ一覧の取得、グループ詳細の取得                                       | サイドバーの「グループ管理」の表示                   |
| `group:write`      | `group`      | `write`    | グループの新規作成、更新（PATCH）、削除                                      | グループ一覧における各種操作ボタンの有効化           |
| `permission:read`  | `permission` | `read`     | 利用可能な権限一覧の取得、グループ一覧の取得                                 | グループ編集画面等での権限選択リストの表示           |
| `permission:write` | `permission` | `write`    | グループへの権限割り当ての変更                                               | グループ作成・更新時における権限変更の許可           |

### バックエンドの認可ロジック（`@PreAuthorize`）

- **Users**:
  - `listUsers`, `getUser`, `getStats`: `user:read` OR `user:write`
  - `createUser`, `patchUser`, `deleteUser`: `user:write`
- **Groups**:
  - `listGroups`: `group:read` OR `group:write` OR `permission:read`
  - `getGroup`: `group:read` OR `group:write`
  - `createGroup`: `group:write` AND `permission:write`
  - `patchGroup`: `group:write` （権限変更を含む場合は追加で `permission:write` が必要）
  - `deleteGroup`: `group:write`

### フロントエンドのアクセス制御

- **ナビゲーション表示**:
  - ダッシュボード / ユーザー管理: `user:read` OR `user:write`
  - グループ管理: `group:read` OR `group:write`
- **ルートガード（`beforeLoad`）**:
  - 権限のないユーザーが `/` または `/users` にアクセスした場合、`/groups` へリダイレクトされます。
- **操作制限**:
  - 権限のないアクション（`write` 等）に対応するボタンは、非活性（disabled）または表示されません。