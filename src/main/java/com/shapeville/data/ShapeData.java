package com.shapeville.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ShapeData {
    // 内部类：封装一个图形（名字 + 图片）
    public static class ShapeItem {
        private final String name;
        private final String imageFilename;

        public ShapeItem(String name, String imageFilename) {
            this.name = name;
            this.imageFilename = imageFilename;
        }

        public String getName() {
            return name;
        }

        public String getImageFilename() {
            return imageFilename;
        }
    }

    // ✅ 2D 图形（中文识图训练）
    private static final List<ShapeItem> shapes2D = Arrays.asList(
            new ShapeItem("Circle", "circle.png"),
            new ShapeItem("Rectangle", "rectangle.png"),
            new ShapeItem("Triangle", "triangle.png"),
            new ShapeItem("Oval", "oval.png"),
            new ShapeItem("Octagon", "octagon.png"),
            new ShapeItem("Square", "square.png"),
            new ShapeItem("Heptagon", "heptagon.png"),
            new ShapeItem("Rhombus", "rhombus.png"),
            new ShapeItem("Pentagon", "pentagon.png"),
            new ShapeItem("Hexagon", "hexagon.png"),
            new ShapeItem("Kite", "kite.png")
    );

    // ✅ 3D 图形（全为高清彩色模型）
    private static final List<ShapeItem> shapes3D = Arrays.asList(
            new ShapeItem("Cube", "cube.png"),
            new ShapeItem("Sphere", "sphere.png"),
            new ShapeItem("Cylinder", "cylinder.png"),
            new ShapeItem("Cone", "cone.png"),
            new ShapeItem("Pyramid", "pyramid.png"),
            new ShapeItem("Rectangular Prism", "rectangular_prism.png"),
            new ShapeItem("Triangular Prism", "triangular_prism.png"),
            new ShapeItem("Torus", "torus.png"),
            new ShapeItem("Tetrahedron", "tetrahedron.png")
    );

    private static final Random random = new Random();

    public static ShapeItem getRandom2DShape() {
        return shapes2D.get(random.nextInt(shapes2D.size()));
    }

    public static ShapeItem getRandom3DShape() {
        return shapes3D.get(random.nextInt(shapes3D.size()));
    }

    public static List<ShapeItem> getAll2DShapes() {
        return Collections.unmodifiableList(shapes2D);
    }

    public static List<ShapeItem> getAll3DShapes() {
        return Collections.unmodifiableList(shapes3D);
    }
}