<!-- 2f930621-1928-46af-8b69-e0ebe17ffcb5 62281650-69c9-4f28-8b80-78eca078a989 -->
# UI 设计系统规范化改造方案（白色主题版）

## 色彩系统调整说明

**核心变更**：

- 主背景色：米棕色 #D9C7AA → **纯白色 #FFFFFF**
- 强调色/主色：深棕色 #5B2C18 → **米棕色 #BAA789**
- 文字色系：棕色系 → **标准灰阶系统**

这样既保留了民俗文化的温润质感（通过 #BAA789），又提升了现代感和清爽度（白色背景）。

---

## 第一阶段：建立全局设计标准（基石）

### 1.1 重构 `colors.xml` - 调整为白色主题色彩系统

**目标**：将应用主题从米棕色调整为白色系，强调色改为 #BAA789

**执行内容** - 修改 `app/src/main/res/values/colors.xml`：

```xml
<!-- 基础背景色 - 白色系 -->
<color name="color_base">#FFFFFF</color>              <!-- 主背景：纯白 -->
<color name="color_base_soft">#FAFAFA</color>          <!-- 次级背景：柔和白 -->

<!-- 边框与分隔 - 浅灰系 -->
<color name="color_frame">#E0E0E0</color>              <!-- 边框：中性灰 -->
<color name="color_frame_soft">#F5F5F5</color>         <!-- 浅边框：极浅灰 -->

<!-- 强调色/主色 - 米棕色 #BAA789 -->
<color name="color_accent">#BAA789</color>             <!-- 主色：米棕（原边框色）-->
<color name="color_accent_dark">#9A8A6F</color>        <!-- 深米棕：按压/悬停态 -->
<color name="color_accent_light">#D4C8B3</color>       <!-- 浅米棕：淡化背景/禁用态 -->

<!-- 表面与卡片 -->
<color name="color_surface_main">#FFFFFF</color>       <!-- 卡片表面：纯白 -->
<color name="color_surface_elevated">#FAFAFA</color>  <!-- 悬浮卡片：浅灰白 -->

<!-- 文本色 - 灰阶系统（适配白色背景）-->
<color name="text_primary">#212121</color>             <!-- 主文字：深灰黑 -->
<color name="text_secondary">#757575</color>           <!-- 次要文字：中灰 -->
<color name="text_tertiary">#9E9E9E</color>            <!-- 三级文字：浅灰 -->
<color name="text_hint">#BDBDBD</color>                <!-- 提示文字：极浅灰 -->
<color name="text_on_accent">#FFFFFF</color>           <!-- 按钮文字：白色（用于 #BAA789 背景上）-->

<!-- 辅助用色 -->
<color name="divider">#E0E0E0</color>                  <!-- 分隔线：与边框同色 -->
<color name="overlay_mask">#80000000</color>           <!-- 遮罩：半透明黑 -->

<!-- 状态色（保持语义，柔和处理）-->
<color name="success">#66BB6A</color>                  <!-- 成功：绿色 -->
<color name="warning">#FFA726</color>                  <!-- 警告：橙色 -->
<color name="error">#EF5350</color>                    <!-- 错误：红色 -->
<color name="info">#42A5F5</color>                     <!-- 信息：蓝色 -->
```

**关键调整**：

1. **背景全部白化**：`color_base` 从 #D9C7AA 改为 #FFFFFF
2. **主色变米棕**：`color_accent` 从 #5B2C18 改为 #BAA789
3. **文字改灰阶**：`text_primary` 从 #5B2C18 改为 #212121，确保对比度
4. **边框改浅灰**：`color_frame` 从 #BAA789 改为 #E0E0E0

**验证标准**：

- 主色 #BAA789 与白色背景对比度 ≥ 3:1（大面积元素符合 WCAG A）
- 文字 #212121 与白色背景对比度 = 16.1:1（远超 WCAG AAA 的 7:1）

---

### 1.2 创建 `dimens.xml` - 统一间距与尺寸系统

**目标**：消除所有硬编码，建立基于 8dp 栅格的标准间距体系

**新建文件**：`app/src/main/res/values/dimens.xml`

**执行内容**：

```xml
<resources>
    <!-- ========== 基础间距（8dp 栅格系统）========== -->
    <dimen name="spacing_xs">4dp</dimen>           <!-- 极小间距 -->
    <dimen name="spacing_small">8dp</dimen>        <!-- 小间距 -->
    <dimen name="spacing_medium">12dp</dimen>      <!-- 中等间距 -->
    <dimen name="spacing_large">16dp</dimen>       <!-- 大间距 -->
    <dimen name="spacing_xl">20dp</dimen>          <!-- 超大间距 -->
    <dimen name="spacing_xxl">24dp</dimen>         <!-- 2倍大间距 -->
    <dimen name="spacing_xxxl">32dp</dimen>        <!-- 3倍大间距 -->
    <dimen name="spacing_huge">48dp</dimen>        <!-- 巨大间距 -->

    <!-- ========== 语义化间距（根据 UI 规范）========== -->
    <dimen name="spacing_page_horizontal">16dp</dimen>      <!-- 页面左右安全间距 -->
    <dimen name="spacing_page_vertical">20dp</dimen>        <!-- 页面上下间距 -->
    <dimen name="spacing_module_vertical">24dp</dimen>      <!-- 模块间垂直间距 -->
    <dimen name="spacing_component_inner">12dp</dimen>      <!-- 组件内部间距 -->
    <dimen name="spacing_grid_item">6dp</dimen>             <!-- 网格列表项间距（半边距，ItemDecoration 会加倍）-->

    <!-- ========== 圆角规格 ========== -->
    <dimen name="corner_radius_button">12dp</dimen>         <!-- 按钮圆角 -->
    <dimen name="corner_radius_card">16dp</dimen>           <!-- 卡片圆角 -->
    <dimen name="corner_radius_card_large">20dp</dimen>     <!-- 大卡片圆角 -->
    <dimen name="corner_radius_search_bar">22dp</dimen>     <!-- 搜索栏圆角（半圆）-->
    <dimen name="corner_radius_chip">16dp</dimen>           <!-- Chip 圆角（半圆）-->

    <!-- ========== 组件固定尺寸 ========== -->
    <dimen name="min_touch_target">44dp</dimen>             <!-- 最小可点击区域（A11y）-->
    <dimen name="button_height">48dp</dimen>                <!-- 标准按钮高度 -->
    <dimen name="toolbar_height">56dp</dimen>               <!-- Toolbar 高度 -->
    <dimen name="search_bar_height">44dp</dimen>            <!-- 搜索栏高度 -->
    <dimen name="chip_height">32dp</dimen>                  <!-- Chip 高度 -->
    <dimen name="bottom_bar_min_height">72dp</dimen>        <!-- 底部栏最小高度 -->
    
    <!-- ========== 图片尺寸 ========== -->
    <dimen name="product_image_height">180dp</dimen>        <!-- 产品卡片图片高度 -->
    <dimen name="cart_item_image_size">96dp</dimen>         <!-- 购物车商品图尺寸 -->
    <dimen name="icon_size_small">20dp</dimen>              <!-- 小图标 -->
    <dimen name="icon_size_medium">24dp</dimen>             <!-- 中图标 -->
    <dimen name="icon_size_large">30dp</dimen>              <!-- 大图标 -->
    <dimen name="avatar_size">88dp</dimen>                  <!-- 头像尺寸 -->

    <!-- ========== 文字尺寸 ========== -->
    <dimen name="text_size_h1">26sp</dimen>                 <!-- H1 主视觉标题 -->
    <dimen name="text_size_h2">20sp</dimen>                 <!-- H2 模块标题 -->
    <dimen name="text_size_h3">16sp</dimen>                 <!-- H3 卡片标题 -->
    <dimen name="text_size_body">14sp</dimen>               <!-- Body 正文 -->
    <dimen name="text_size_caption">12sp</dimen>            <!-- Caption 标签 -->
    <dimen name="text_size_price">20sp</dimen>              <!-- 价格专用 -->
</resources>
```

**验证标准**：所有数值都有语义化命名，避免直接使用数字命名

---

### 1.3 完善 `styles.xml` - 建立 TextAppearance 体系

**目标**：统一所有文本样式，消除 TextView 中的硬编码字号和颜色

**执行内容** - 在现有 `app/src/main/res/values/styles.xml` 中**追加**：

```xml
<!-- ========== TextAppearance 文字样式体系 ========== -->
<style name="TextAppearance.MGG.Headline1" parent="TextAppearance.Material3.HeadlineLarge">
    <item name="android:textSize">@dimen/text_size_h1</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="fontFamily">sans-serif-medium</item>
    <item name="android:fontFamily">sans-serif-medium</item>
</style>

<style name="TextAppearance.MGG.Headline2" parent="TextAppearance.Material3.HeadlineMedium">
    <item name="android:textSize">@dimen/text_size_h2</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="fontFamily">sans-serif-medium</item>
    <item name="android:fontFamily">sans-serif-medium</item>
</style>

<style name="TextAppearance.MGG.Headline3" parent="TextAppearance.Material3.TitleMedium">
    <item name="android:textSize">@dimen/text_size_h3</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="fontFamily">sans-serif-medium</item>
    <item name="android:fontFamily">sans-serif-medium</item>
</style>

<style name="TextAppearance.MGG.Body" parent="TextAppearance.Material3.BodyLarge">
    <item name="android:textSize">@dimen/text_size_body</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="fontFamily">sans-serif</item>
    <item name="android:fontFamily">sans-serif</item>
</style>

<style name="TextAppearance.MGG.BodySecondary" parent="TextAppearance.Material3.BodyLarge">
    <item name="android:textSize">@dimen/text_size_body</item>
    <item name="android:textColor">@color/text_secondary</item>
    <item name="fontFamily">sans-serif</item>
    <item name="android:fontFamily">sans-serif</item>
</style>

<style name="TextAppearance.MGG.Caption" parent="TextAppearance.Material3.BodySmall">
    <item name="android:textSize">@dimen/text_size_caption</item>
    <item name="android:textColor">@color/text_secondary</item>
    <item name="fontFamily">sans-serif</item>
    <item name="android:fontFamily">sans-serif</item>
</style>

<style name="TextAppearance.MGG.Price" parent="TextAppearance.Material3.TitleLarge">
    <item name="android:textSize">@dimen/text_size_price</item>
    <item name="android:textColor">@color/color_accent</item>
    <item name="fontFamily">sans-serif-medium</item>
    <item name="android:fontFamily">sans-serif-medium</item>
</style>

<!-- ========== 更新按钮样式使用新颜色 ========== -->
<style name="Widget.MGG.Button.Primary" parent="Widget.Material3.Button">
    <item name="backgroundTint">@color/color_accent</item>
    <item name="android:textColor">@color/text_on_accent</item>
    <item name="cornerRadius">@dimen/corner_radius_button</item>
    <item name="android:minHeight">@dimen/button_height</item>
    <item name="android:textAppearance">@style/TextAppearance.MGG.Body</item>
</style>

<style name="Widget.MGG.Button.Secondary" parent="Widget.Material3.Button.OutlinedButton">
    <item name="strokeColor">@color/color_accent</item>
    <item name="android:textColor">@color/color_accent</item>
    <item name="cornerRadius">@dimen/corner_radius_button</item>
    <item name="android:minHeight">@dimen/button_height</item>
</style>
```

---

### 1.4 更新 `themes.xml` - 应用新色彩系统

**执行内容** - 修改 `app/src/main/res/values/themes.xml` 的 `Base.Theme.MGG`：

```xml
<style name="Base.Theme.MGG" parent="Theme.Material3.DayNight.NoActionBar">
    <!-- 主色系（米棕色 #BAA789）-->
    <item name="colorPrimary">@color/color_accent</item>
    <item name="colorOnPrimary">@color/text_on_accent</item>
    <item name="colorPrimaryContainer">@color/color_accent_light</item>
    <item name="colorOnPrimaryContainer">@color/text_primary</item>
    
    <!-- 次要色系 -->
    <item name="colorSecondary">@color/color_frame</item>
    <item name="colorOnSecondary">@color/text_primary</item>
    
    <!-- 表面色系（白色）-->
    <item name="colorSurface">@color/color_surface_main</item>
    <item name="colorOnSurface">@color/text_primary</item>
    <item name="android:colorBackground">@color/color_base</item>
    <item name="colorOnBackground">@color/text_primary</item>
    
    <!-- 边框 -->
    <item name="colorOutline">@color/color_frame</item>
    
    <!-- 系统栏颜色（状态栏使用米棕深色，导航栏使用白色）-->
    <item name="android:statusBarColor" tools:targetApi="21">@color/color_accent_dark</item>
    <item name="android:navigationBarColor" tools:targetApi="21">@color/color_base</item>
    <item name="android:windowLightNavigationBar" tools:targetApi="26">true</item>
    
    <!-- 默认组件样式映射 -->
    <item name="materialButtonStyle">@style/Widget.MGG.Button.Primary</item>
    <item name="textAppearanceBodyLarge">@style/TextAppearance.MGG.Body</item>
</style>
```

---

## 第二阶段：提取可复用组件

### 2.1 创建通用 Toolbar 组件

**新建文件**：`app/src/main/res/layout/view_common_toolbar.xml`

```xml
<com.google.android.material.appbar.MaterialToolbar
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/toolbar"
    android:layout_width="match_parent"
    android:layout_height="@dimen/toolbar_height"
    android:background="@color/color_base"
    android:elevation="0dp"
    app:navigationIcon="@drawable/ic_arrow_back"
    app:titleTextAppearance="@style/TextAppearance.MGG.Headline3"
    app:titleTextColor="@color/text_primary">
    
    <!-- 底部分隔线 -->
    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_gravity="bottom"
        android:background="@color/color_frame" />
</com.google.android.material.appbar.MaterialToolbar>
```

**应用场景**：订单列表、地址管理、产品详情等所有二级页面

---

### 2.2 创建底部结算栏组件

**新建文件**：`app/src/main/res/layout/view_bottom_checkout_bar.xml`

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:minHeight="@dimen/bottom_bar_min_height"
    android:background="@color/color_base"
    android:padding="@dimen/spacing_large">
    
    <!-- 顶部分隔线 -->
    <View
        android:id="@+id/divider"
        android:layout_width="0dp"
        android:layout_height="1dp"
        android:background="@color/color_frame"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
    
    <LinearLayout
        android:id="@+id/layoutTotal"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:layout_marginTop="@dimen/spacing_medium"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/divider"
        app:layout_constraintBottom_toBottomOf="parent">
        
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="总计："
            android:textAppearance="@style/TextAppearance.MGG.Caption" />
        
        <TextView
            android:id="@+id/tvTotalPrice"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="¥0.00"
            android:textAppearance="@style/TextAppearance.MGG.Price" />
    </LinearLayout>
    
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btnCheckout"
        style="@style/Widget.MGG.Button.Primary"
        android:layout_width="0dp"
        android:layout_height="@dimen/button_height"
        android:text="去结算"
        android:layout_marginStart="@dimen/spacing_large"
        android:layout_marginTop="@dimen/spacing_medium"
        app:layout_constraintStart_toEndOf="@id/layoutTotal"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toBottomOf="@id/divider"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintWidth_min="150dp" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**应用场景**：购物车页、订单确认页

---

### 2.3 创建个人中心菜单项组件

**新建文件**：`app/src/main/res/layout/view_profile_menu_item.xml`

```xml
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="@dimen/min_touch_target"
    android:background="?attr/selectableItemBackground"
    android:paddingStart="@dimen/spacing_medium"
    android:paddingEnd="@dimen/spacing_medium"
    android:paddingTop="@dimen/spacing_small"
    android:paddingBottom="@dimen/spacing_small">
    
    <ImageView
        android:id="@+id/ivMenuIcon"
        android:layout_width="@dimen/icon_size_medium"
        android:layout_height="@dimen/icon_size_medium"
        android:tint="@color/color_accent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />
    
    <TextView
        android:id="@+id/tvMenuTitle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="@dimen/spacing_large"
        android:textAppearance="@style/TextAppearance.MGG.Body"
        app:layout_constraintStart_toEndOf="@id/ivMenuIcon"
        app:layout_constraintEnd_toStartOf="@id/ivArrow"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />
    
    <ImageView
        android:id="@+id/ivArrow"
        android:layout_width="@dimen/icon_size_small"
        android:layout_height="@dimen/icon_size_small"
        android:rotation="270"
        android:tint="@color/text_tertiary"
        android:src="@android:drawable/arrow_down_float"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />
</androidx.constraintlayout.widget.ConstraintLayout>
```

**应用场景**：`fragment_profile.xml` 的菜单项

---

## 第三阶段：重构核心列表项（List Items）

### 3.1 重构 `item_product.xml` - 产品卡片

**改造方案**：

- 根布局：MaterialCardView（保持）
- 内部布局：LinearLayout → **ConstraintLayout**（扁平化）
- 所有硬编码 → `@dimen/` 和 `@style/`
- 卡片背景色改为 `color_surface_main`（白色）
- 边框色改为 `color_frame`（浅灰）
- 价格颜色改为 `color_accent`（米棕色）

**关键修改点**：

- `android:layout_margin="6dp"` → `@dimen/spacing_grid_item`
- `android:padding="16dp"` → `@dimen/spacing_large`
- `android:textSize="16sp"` → `android:textAppearance="@style/TextAppearance.MGG.Headline3"`
- `android:textColor="@color/color_accent"` → 由 `TextAppearance.MGG.Price` 自动指定
- `app:cardCornerRadius="18dp"` → `@dimen/corner_radius_card`

---

### 3.2 重构 `item_cart.xml` - 购物车条目

**改造方案**：

- 内部改为 ConstraintLayout，布局约束：
  - CheckBox（左） → ProductImage（左中） → InfoLayout（右） → DeleteButton（最右）
  - QuantityControl 约束到 InfoLayout 底部
- 所有尺寸使用 `@dimen/`
- 所有文字使用 `@style/TextAppearance.MGG.*`
- 背景色 `color_surface_main`，边框 `color_frame`

---

### 3.3 重构其他列表项

**执行策略**：`item_order.xml`, `item_article.xml`, `item_review.xml` 同上

---

## 第四阶段：重构核心页面布局

### 4.1 优化 `fragment_home.xml` - 首页

**改造重点**：

1. **整体背景色**：`android:background="@color/color_base"`（白色）
2. **问候区**：文字使用 `TextAppearance.MGG.Headline2` 和 `BodySecondary`
3. **搜索栏**：高度 `@dimen/search_bar_height`，背景改为浅灰 `color_frame_soft`
4. **卡片区域**：所有 MaterialCardView 背景 `color_surface_main`，边框 `color_frame`
5. **按钮**：使用 `Widget.MGG.Button.Primary`（米棕色背景）
6. **间距统一**：所有 margin/padding 使用 `@dimen/spacing_*`

---

### 4.2 优化 `fragment_profile.xml` - 个人中心

**改造重点**：

1. **背景色**：`color_base_soft`（浅灰白 #FAFAFA），与纯白卡片形成层次
2. **用户卡片**：背景 `color_surface_main`（白色），边框 `color_frame`
3. **菜单项**：使用 `<include layout="@layout/view_profile_menu_item" />`
4. **图标颜色**：改为 `color_accent`（米棕色）
5. **间距统一**：使用 `@dimen/spacing_*`

---

### 4.3-4.5 其他页面优化

**统一原则**：

- 所有页面背景 → `color_base` 或 `color_base_soft`
- 所有卡片背景 → `color_surface_main`，边框 → `color_frame`
- 所有主按钮 → `Widget.MGG.Button.Primary`（米棕色）
- 所有文字 → `TextAppearance.MGG.*`
- 所有间距 → `@dimen/spacing_*`

---

## 第五阶段：细节优化与收尾

### 5.1 统一所有 Activity 的 Toolbar

使用 `<include layout="@layout/view_common_toolbar" />`

**涉及文件**：7+ 个 Activity

---

### 5.2 底部导航栏样式优化

**优化内容**：

- 背景色：`color_base`（白色）
- 顶部分隔线：`color_frame`（浅灰）
- 选中颜色：`color_accent`（米棕色）
- 未选中颜色：`text_secondary`

---

### 5.3 创建空状态组件 + 5.4 资源优化 + 5.5 最终检查

（内容同原计划）

---

## 交付成果

### 修改的资源文件（3 个）

1. **`values/colors.xml`** - 重构为白色主题 + 米棕主色
2. **`values/styles.xml`** - 新增 7 个 TextAppearance 样式
3. **`values/themes.xml`** - 更新主题色系映射

### 新增资源文件（4 个）

4. **`values/dimens.xml`** - 尺寸标准库（约 40 个定义）
5. **`layout/view_common_toolbar.xml`** - 通用 Toolbar
6. **`layout/view_bottom_checkout_bar.xml`** - 结算栏
7. **`layout/view_profile_menu_item.xml`** - 菜单项

### 重构的布局文件（20+ 个）

- 所有列表项、Fragment、Activity 布局

### 视觉效果对比

**改造前**：

- 米棕色背景 #D9C7AA + 深棕主色 #5B2C18
- 整体偏暖偏重，民俗感强但略显沉闷

**改造后**：

- 白色背景 #FFFFFF + 米棕主色 #BAA789
- 清爽现代，米棕色点缀保留温润质感
- 文字对比度提升，可读性更强

---

## 执行顺序

**严格按阶段执行**，第一阶段是所有后续工作的基础：

1. **第一阶段**（1.5小时）：色彩系统 + dimens + styles + themes
2. **第二阶段**（1小时）：可复用组件
3. **第三阶段**（2-3小时）：列表项重构
4. **第四阶段**（3-4小时）：页面重构
5. **第五阶段**（1-2小时）：细节优化

**总工时**：8-12 小时

---

## 风险与注意事项

1. **色彩对比度验证**：确保 #BAA789 在白色背景上清晰可见（建议用于大面积按钮、图标，不用于小字）
2. **夜间模式考虑**：当前方案基于日间模式，如需夜间模式，需在 `values-night/` 创建对应资源
3. **品牌一致性**：新色彩方案已偏离原 UI 规范的米棕色调背景，请与设计师确认
4. **测试要求**：每阶段完成后必须在真机验证视觉效果和对比度

### To-dos

- [ ] 创建 dimens.xml 定义所有标准间距、圆角、字号
- [ ] 在 styles.xml 中创建完整的 TextAppearance 体系（6+ 个层级）
- [ ] 增强 themes.xml 配置全局默认样式
- [ ] 创建通用 Toolbar 组件（view_common_toolbar.xml）
- [ ] 创建底部结算栏组件（view_bottom_checkout_bar.xml）
- [ ] 创建个人中心菜单项组件（view_profile_menu_item.xml）
- [ ] 重构 item_product.xml 为 ConstraintLayout，消除硬编码
- [ ] 重构 item_cart.xml 为 ConstraintLayout，消除硬编码
- [ ] 重构 item_order.xml, item_article.xml, item_review.xml
- [ ] 优化 fragment_home.xml，统一间距和文字样式
- [ ] 重构 fragment_profile.xml，应用可复用菜单组件
- [ ] 优化 activity_login.xml 和 activity_register.xml
- [ ] 优化 fragment_category.xml 和 fragment_community.xml
- [ ] 统一所有二级页面的 Toolbar（7+ 个 Activity）
- [ ] 优化底部导航栏样式符合 UI 规范
- [ ] 创建标准化空状态组件
- [ ] 执行最终检查清单，清理未使用资源，运行 Lint