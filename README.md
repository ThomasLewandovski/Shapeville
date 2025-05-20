# Shapeville 🎓

A Java-based mini project for primary-level geometry recognition, built using **Swing GUI** and **Maven**.

## 📦 Project Structure

- `com.shapeville.data` — 图形与角度数据源（题库）
- `com.shapeville.ui` — Swing GUI 界面（主界面、任务面板）
- `com.shapeville.tasks` — 控制台测试版任务逻辑
- `com.shapeville.manager` — 积分管理器、游戏控制器

## ✅ Features

- 2D 图形识别任务（Circle, Triangle, etc.）
- 3D 图形识别任务（Cube, Sphere, etc.）
- 自动计分、答题次数控制
- 支持切换任务、返回主菜单
- 支持后续扩展：角度分类 / 面积计算

## 🚀 How to Run

```bash
git clone git@github.com:ThomasLewandovski/Shapeville.git
cd Shapeville
mvn clean compile
mvn exec:java -Dexec.mainClass="com.shapeville.ui.MainFrame"
```

## javadocs 

- 生成 **Javadocs**：在项目根目录下，打开命令行终端，执行`mvn javadoc:javadoc`命令。**Maven** 会根据项目中的代码注释生成 **Javadoc** 文档。
- 打开文档：生成完成后，在项目的**target/site/apidocs/**目录下找到**index.html**文件，使用浏览器打开该文件，即可查看生成的 **Javadoc** 文档，了解项目的 API 接口及相关说明。
