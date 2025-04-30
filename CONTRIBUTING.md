# 👥 Contributor Guide for Shapeville

欢迎来到 Shapeville 项目协作开发！以下是小组协作规范，请所有成员遵守。

---

## 📁 目录结构简介
```
shapeville/
├── src/main/java/com/shapeville/  # 所有 Java 包结构
├── src/main/resources/images/     # 所有图形图像
├── pom.xml                        # Maven 配置
├── .gitignore
├── README.md
└── CONTRIBUTING.md
---
```
## 🚀 Git 协作流程

### ✅ 1. 克隆项目

```bash
git clone git@github.com:ThomasLewandovski/Shapeville.git
cd Shapeville
```
### ✅ 2. 每人创建自己的开发分支（不要直接在 master 改）
```
git checkout -b feature-你的名字-模块名

git checkout -b feature-xiaoming-angle-task
```

### ✅ 3. 正常编码后，提交并推送
```
git add .
git commit -m "✨ 完成角度任务面板逻辑"
git push -u origin feature-mkh-angle-task
```

###✅ 4. 发起 Pull Request（PR）合并代码

   在 GitHub 上点击绿色按钮「Compare & pull request」提交合并申请。组长或大家评审后通过。

✅ 提交信息规范（建议使用 Emoji）
```
- ✨ 新功能
- 🐛 修复 bug
- 📚 文档更新
- 🎨 UI优化
- 🧹 代码清理
- 🧪 测试
- ⚡️ 性能优化
- 📝 注释
- 📋 代码格式化
- 🔧 工具配置
- 🔒 安全修复
- 📦 依赖更新
- 🔧 构建脚本
- 🔧 配置文件
- 📝 日志
```
示例
```
✨ 添加图形识别面板
🐛 修复得分无法更新问题
🎨 美化主界面字体和按钮
📝 补充 README 与协作说明
```

### 🧪 测试 & 提交注意事项
	•	每次 push 前请确认能正常运行
	•	不要提交 .idea/ 或 target/ 等文件（由 .gitignore 管控）
	•	如果新增图片，请放在 src/main/resources/images/

### 👨‍👩‍👧‍👦 当前组员（可补充）
姓名
```
ThomasLewandovski
```