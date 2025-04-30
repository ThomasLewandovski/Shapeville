# 🧭 Shapeville 项目协作手册

---

## 📦 1. 项目克隆（首次使用）

### ✅ 使用 SSH 克隆项目到本地

```bash
git clone git@github.com:ThomasLewandovski/Shapeville.git
cd Shapeville
```

如果你尚未配置 SSH，请联系组长或查看 CONTRIBUTING.md 的 SSH 配置教程。

## 💡 2. IntelliJ IDEA 分支切换为 master

❗ 默认你 clone 下来的分支是 main，但我们使用 master

✅ 步骤如下：
1.	打开 IntelliJ IDEA
2.	在右下角找到 Git 分支按钮
3.	点击 main → 选择 New Branch from...
4.	输入新分支名 master，来源选 main
5.	点击 Create and Checkout
6.	IDEA 会自动切换到 master，并在右下角显示

## 🔄 3. 每次开发流程（建议分支开发）
### 1️⃣ 拉取最新主分支
```
git checkout master
git pull
```

### 2️⃣ 创建你自己的开发分支
```angular2html
git checkout -b feature-你的名字-模块名
例如：
git checkout -b feature-mkh-task2
```

### 3️⃣ 编码 + 添加改动
```angular2html
git add .
git commit -m "✨ 完成任务2逻辑"

```

### 4️⃣ 推送你的开发分支
```angular2html
git push -u origin feature-你的名字-模块名
```

## 🧪️ 4.  编译 & 运行（Maven）
```angular2html
mvn clean compile
mvn exec:java -Dexec.mainClass="com.shapeville.ui.MainFrame"
```

## 🧯 5. 常见问题
问题                                  解决方法
推送时提示权限错误                      确保已设置 SSH Key 并添加到 GitHub
IDEA 无法切换分支                      点击右下角 Git 分支 → 切换为 master
.gitignore 不生效                     必须在 git add 之前创建好
文件不显示在 GitHub                    确认是否 git push 到了 origin/master

## 👨‍💻 6. 分支命名建议
```angular2html
feature-名字-任务       ✅ 开发新功能
fix-名字-问题描述       🐛 修 Bug
doc-名字-readme更新     📝 文档更新
```

## ✅ 推荐参考文件
	•	.gitignore — 忽略 IDEA 缓存、target 等
	•	README.md — 项目简介
	•	CONTRIBUTING.md — 多人协作规范
	•	HELPER.md — 本文件，用于新手入门！