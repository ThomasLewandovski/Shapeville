package com.shapeville.data;

import java.io.Serializable;
import java.util.*;

public class ShapeData {
    // 内部类：封装一个图形（名字 + 图片）
    public static class ShapeItem implements Serializable {
        private static final long serialVersionUID = 1L;
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

    public static class Shape2D extends ShapeItem implements Serializable {
        private static final long serialVersionUID = 2L;

        public Shape2D(String name, String imageFilename) {
            super(name, imageFilename);
        }
    }

    public static class Shape3D extends ShapeItem implements Serializable {
        private static final long serialVersionUID = 3L;

        public Shape3D(String name, String imageFilename) {
            super(name, imageFilename);
        }
    }

    // ✅ 2D 图形（中文识图训练）
    private static final List<Shape2D> shapes2D = Arrays.asList(
            new Shape2D("Circle", "circle.png"),
            new Shape2D("Rectangle", "rectangle.png"),
            new Shape2D("Triangle", "triangle.png"),
            new Shape2D("Oval", "oval.png"),
            new Shape2D("Octagon", "octagon.png"),
            new Shape2D("Square", "square.png"),
            new Shape2D("Heptagon", "heptagon.png"),
            new Shape2D("Rhombus", "rhombus.png"),
            new Shape2D("Pentagon", "pentagon.png"),
            new Shape2D("Hexagon", "hexagon.png"),
            new Shape2D("Kite", "kite.png")
    );

    // ✅ 3D 图形（全为高清彩色模型）
    private static final List<Shape3D> shapes3D = Arrays.asList(
            new Shape3D("Cube", "cube.png"),
            new Shape3D("Sphere", "sphere.png"),
            new Shape3D("Cylinder", "cylinder.png"),
            new Shape3D("Cone", "cone.png"),
            new Shape3D("Pyramid", "pyramid.png"),
            new Shape3D("Cuboid", "cuboid.png"),
            new Shape3D("Triangular Prism", "triangular_prism.png"),
            new Shape3D("Tetrahedron", "tetrahedron.png")
    );

    public static List<Shape2D> getAll2DShapes() {
        return Collections.unmodifiableList(shapes2D);
    }

    public static List<Shape3D> getAll3DShapes() {
        return Collections.unmodifiableList(shapes3D);
    }
}