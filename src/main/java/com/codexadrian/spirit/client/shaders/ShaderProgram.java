package com.codexadrian.spirit.client.shaders;

import com.codexadrian.spirit.Spirit;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL43.*;

public abstract class ShaderProgram {

    private FloatBuffer matrixBuffer;
    protected ResourceLocation vertShader;
    protected ResourceLocation fragShader;

    protected int programId;

    public ShaderProgram(ResourceLocation vertShader, ResourceLocation fragShader) {
        this.vertShader = vertShader;
        this.fragShader = fragShader;
    }

    public void init() {
        matrixBuffer = BufferUtils.createFloatBuffer(16);
        programId = glCreateProgram();

        if(vertShader != null) {
            int vertShaderId = createShader(vertShader, GL_VERTEX_SHADER);
            glAttachShader(programId, vertShaderId);
        }

        if(fragShader != null) {
            int fragShaderId = createShader(fragShader, GL_FRAGMENT_SHADER);
            glAttachShader(programId, fragShaderId);
        }

        bindAttributes();
        glLinkProgram(programId);

        this.getAllUniformLocations();
    }

    public void start() {
        glUseProgram(programId);
    }

    public void stop() {
        glUseProgram(0);
    }

    public abstract void getAllUniformLocations();

    public int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programId, uniformName);
    }

    public void bindAttributes() {
    }

    protected void bindAttribute(int attribute, String variableName) {
        glBindAttribLocation(programId, attribute, variableName);
    }

    protected void loadFloat(int location, float value) {
        glUniform1f(location, value);
    }

    protected void loadInt(int location, int value) {
        glUniform1i(location, value);
    }

    //Minecraft Vector stuff
    protected void loadVector(int location, Vec2 vector) {
        glUniform2f(location, vector.x, vector.y);
    }

    protected void loadVector(int location, Vec3 vector) {
        glUniform3f(location, (float) vector.x, (float) vector.y, (float) vector.z);
    }

    protected void loadVector(int location, Vector3f vector) {
        glUniform3f(location, vector.x(), vector.y(), vector.z());
    }

    protected void loadVector(int location, Vector4f vector) {
        glUniform4f(location, vector.x(), vector.y(), vector.z(), vector.w());
    }

    protected void loadMatrix(int location, Matrix4f matrix) {
        matrix.store(matrixBuffer);
        glUniformMatrix4fv(location, false, matrixBuffer);
    }

    private int createShader(ResourceLocation location, int shaderType) {
        int shader = glCreateShader(shaderType);
        if(shader == 0) return 0;
        try {
            glShaderSource(shader, readFileAsString(location));
        }catch (Exception e) {
            e.printStackTrace();
        }

        glCompileShader(shader);
        if(glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            Spirit.LOGGER.fatal("Could not compile shader for " + location.toString() + "!");
            throw new RuntimeException("Error creating shader: " + glGetShaderInfoLog(shader));
        }

        return shader;
    }

    private String readFileAsString(ResourceLocation location) {
        InputStream shaderStream = getShaderStream(location);
        String s = "";

        if(shaderStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(shaderStream, "UTF-8"))) {
                s = reader.lines().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                Spirit.LOGGER.fatal("Unable to read shader file! Source: " + location.toString(), e);
            }
        }

        return s;
    }

    private InputStream getShaderStream(ResourceLocation location) {
        if(Minecraft.getInstance().getResourceManager().hasResource(location)) {
            try {
                return Minecraft.getInstance().getResourceManager().getResource(location).getInputStream();
            }catch (IOException e) {
                Spirit.LOGGER.fatal("Unable to read shader file! Source: " + location.toString(), e);
                return null;
            }
        }else {
            Spirit.LOGGER.fatal("Unable to locate shader file! Source: " + location.toString());
            return null;
        }
    }
}
