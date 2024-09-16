package ru.nedan.functional;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.*;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * Класс для рендера шейдеров
 */

public class Shader {
    private static final Minecraft mc = Minecraft.getInstance();
    private final int programID;

    public Shader(String fragmentShaderLoc) {
        int program = glCreateProgram();
        int fragmentShaderID;

        if (fragmentShaderLoc.equals("rounded")) {
            fragmentShaderID = createShader(new ByteArrayInputStream(rounded.getBytes()), GL_FRAGMENT_SHADER);
        } else {
            throw new IllegalStateException();
        }

        glAttachShader(program, fragmentShaderID);

        String vertex = "#version 120\n" +
                "\n" +
                "void main() {\n" +
                "    gl_TexCoord[0] = gl_MultiTexCoord0;\n" +
                "    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;\n" +
                "}";

        int vertexShaderID = createShader(new ByteArrayInputStream(vertex.getBytes()), GL_VERTEX_SHADER);
        glAttachShader(program, vertexShaderID);


        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new IllegalStateException("Shader failed to link!");
        }

        this.programID = program;
    }

    public void init() {
        glUseProgram(programID);
    }

    public void unload() {
        glUseProgram(0);
    }

    public void setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1:
                glUniform1f(loc, args[0]);
                break;
            case 2:
                glUniform2f(loc, args[0], args[1]);
                break;
            case 3:
                glUniform3f(loc, args[0], args[1], args[2]);
                break;
            case 4:
                glUniform4f(loc, args[0], args[1], args[2], args[3]);
                break;
        }
    }

    public void setUniformfb(String name, FloatBuffer buffer) {
        GL30.glUniform1fv(GL30.glGetUniformLocation(programID, name), buffer);
    }

    public static void drawQuads(double x, double y, double width, double height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2d(x, y);
        glTexCoord2f(0, 1);
        glVertex2d(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2d(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2d(x + width, y);
        glEnd();
    }

    public static void drawQuads() {
        MainWindow sr = mc.getWindow();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(0, 0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0F, (float) Math.max(sr.getGuiScaledHeight(),1));
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f((float) Math.max(sr.getGuiScaledWidth(), 1), (float) Math.max(sr.getGuiScaledHeight(),1));
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f((float) Math.max(sr.getGuiScaledWidth(),1), 0);
        GL11.glEnd();
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readInputStream(inputStream));
        glCompileShader(shader);


        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile!", shaderType));
        }

        return shader;
    }

    public static String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null)
                stringBuilder.append(line).append('\n');

        } catch (Exception ignored) {

        }
        return stringBuilder.toString();
    }

    private final String rounded = "#version 120\n" +
            "\n" +
            "uniform vec2 location, rectSize;\n" +
            "uniform vec4 color;\n" +
            "uniform float radius;\n" +
            "uniform bool blur;\n" +
            "\n" +
            "float roundSDF(vec2 p, vec2 b, float r) {\n" +
            "    return length(max(abs(p) - b, 0.0)) - r;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "void main() {\n" +
            "    vec2 rectHalf = rectSize * .5;\n" +
            "    // Smooth the result (free antialiasing).\n" +
            "    float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;\n" +
            "    gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);\n" +
            "\n" +
            "}";

    private final String roundedGradient = "#version 120\n" +
            "\n" +
            "uniform float round;\n" +
            "uniform vec2 size;\n" +
            "uniform vec4 color1;\n" +
            "uniform vec4 color2;\n" +
            "uniform vec4 color3;\n" +
            "uniform vec4 color4;\n" +
            "\n" +
            "float alpha(vec2 d, vec2 d1) {\n" +
            "    vec2 v = abs(d) - d1 + round;\n" +
            "    return min(max(v.x, v.y), 0.0) + length(max(v, .0f)) - round;\n" +
            "}\n" +
            "\n" +
            "void main() {\n" +
            "\t vec2 coords = gl_TexCoord[0].st;\n" +
            "    vec2 centre = .5f * size;\n" +
            "    vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);\n" +
            "    gl_FragColor = vec4(color.rgb, color.a * (1.f- smoothstep(0.f, 1.5f, alpha(centre - (gl_TexCoord[0].st * size), centre - 1.f))));\n" +
            "}\n";

    private final String bloom = "#version 120\n" +
            "\n" +
            "uniform sampler2D sampler1;\n" +
            "uniform sampler2D sampler2;\n" +
            "uniform vec2 texelSize;\n" +
            "uniform vec2 direction;\n" +
            "uniform float radius;\n" +
            "uniform float kernel[64];\n" +
            "\n" +
            "void main(void)\n" +
            "{\n" +
            "    vec2 uv = gl_TexCoord[0].st;\n" +
            "\n" +
            "    if (direction.x == 0.0 && texture2D(sampler2, uv).a > 0.0) {\n" +
            "    \tdiscard;\n" +
            "    }\n" +
            "\n" +
            "    vec4 pixel_color = texture2D(sampler1, uv);\n" +
            "    pixel_color.rgb *= pixel_color.a;\n" +
            "    pixel_color *= kernel[0];\n" +
            "\n" +
            "    for (float f = 1; f <= radius; f++) {\n" +
            "        vec2 offset = f * texelSize * direction;\n" +
            "        vec4 left = texture2D(sampler1, uv - offset);\n" +
            "        vec4 right = texture2D(sampler1, uv + offset);\n" +
            "        left.rgb *= left.a;\n" +
            "        right.rgb *= right.a;\n" +
            "        pixel_color += (left + right) * kernel[int(f)];\n" +
            "    }\n" +
            "\n" +
            "    gl_FragColor = vec4(pixel_color.rgb / pixel_color.a, pixel_color.a);\n" +
            "}\n";

}