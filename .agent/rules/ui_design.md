---
trigger: glob
globs: frontend/**/*
---

## デザイン・UI 仕様書（Rough Specification）

このプロジェクトでは、モダンでプレミアムな業務管理システムを目指す。エージェントは以下のガイドラインに従って UI を構築すること。

### 1. デザインコンセプト
- **Modern Enterprise**: 清潔感があり、信頼感を感じさせるデザイン。
- **High Contrast & Clarity**: 重要な情報が埋もれないよう、コントラストを適切に配置する。
- **Premium Feel**: 微細なグラデーション、シャドウ、タイポグラフィの調整により、安っぽさを排除する。

### 2. カラーパレット
- **Primary**: Indigo (#4f46e5) 〜 Violet (#7c3aed)。アクションボタンやアクセントに使用。
- **Sidebar**: Slate 900 (#0f172a)。ダークテーマを採用し、メインコンテンツとの境界を明確にする。
- **Background**: Gray 50 (#f9fafb) または Gray 100 (#f3f4f6)。目に優しい薄いグレーをベースとする。
- **Surface (Cards)**: White (#ffffff)。コンテンツはカード形式で浮き上がらせる。
- **Text**:
  - Primary: Slate 900 (#0f172a)
  - Secondary: Slate 500 (#64748b)
- **Status Colors**:
  - Success: Emerald 600
  - Warning: Amber 500
  - Error: Rose 600

### 3. レイアウトパターン
- **Sidebar Layout**: 左側に固定幅のサイドバー (Slate-900)、右側にメインコンテンツ。
- **Header**: メイン領域上部に固定のヘッダー。ユーザーメニュー、パンくずリスト等を配置。
- **Content Area**: `max-w-7xl` 程度のコンテナ内に、カード形式で各機能を配置。
- **Responsiveness**: デスクトップ第一だが、モバイルでも破綻しないレスポンシブ設計（ハンバーガーメニュー等）。

### 4. タイポグラフィ
- **Font**: "Inter", system-ui, sans-serif を優先。
- **Weight**: 見出しには `font-semibold` または `bold`、本文には `font-normal`。
- **Size**: ベースは `text-sm` (14px) または `text-base` (16px)。

### 5. コンポーネントスタイル (shadcn/ui ベース)
- **Buttons**: `rounded-md` を基本とし、プライマリボタンには微かなシャドウ (`shadow-sm`) を付与。
- **Cards**: `rounded-xl` または `rounded-lg`。境界線 (`border`) と非常に薄いシャドウ (`shadow-sm`) を組み合わせる。
- **Forms**: 入力フィールドにはフォーカス時にプライマリカラーのリング (`ring-primary`) を表示。
- **Tables**: ストライプ、またはホバー時の行ハイライトを適用。ヘッダーは `bg-gray-50` で固定。

### 6. インタラクション & アニメーション
- **Hover Effects**: ボタンやリンクには `transition-all` と適切な `hover:bg-*` を設定。
- **Loading**: データ取得中は `Skeleton` コンポーネントを使用してレイアウトシフトを防ぐ。
- **Feedback**: 成功/失敗時には必ず `Toast` (Sonner 等) でフィードバックを行う。
