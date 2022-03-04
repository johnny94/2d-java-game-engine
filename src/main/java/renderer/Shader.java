package renderer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

public final class Shader {
    private enum ShaderType {
        VERTEX, FRAGMENT;

        private static final Map<String, ShaderType> stringToEnum =
                Stream.of(values()).collect(Collectors.toMap(Enum::name, Function.identity()));

        public static Optional<ShaderType> fromString(String name) {
            return Optional.ofNullable(stringToEnum.get(name));
        }
    }

    private boolean beingUsed;

    private int shaderProgramId;
    private final String filepath;
    private final Map<ShaderType, String> shaderSource = new EnumMap<>(ShaderType.class);

    public Shader(String filepath) {
        this.filepath = filepath;

        try {
            String source = String.join("\n", Files.readAllLines(Paths.get(filepath)));

            // We will write vertex and fragment shader in a single file.
            // Those shaders are separated by #type SHADERTYPE.
            // With this String#split(), the first element in shader file will be an empty string
            // so have to start from the second element;
            String[] splitShader = source.split("(#type)( )+(\\w)+");
            int curShader = 1;

            int startPos = 0;
            int endPos = 0;
            while(curShader < splitShader.length) {
                startPos = source.indexOf("#type", endPos) + 6;
                endPos = source.indexOf(System.lineSeparator(), startPos);
                String shaderName = source.substring(startPos, endPos)
                                          .trim()
                                          .toUpperCase();
                ShaderType type = ShaderType.fromString(shaderName)
                        .orElseThrow(() -> new RuntimeException("Unknown shader type: '" + shaderName + "'"));

                shaderSource.put(type, splitShader[curShader]);
                curShader++;
            }

        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open shader file '" + filepath + "'";
        }
    }

    public void compile() {
        int vertexId, fragmentId;
        // Vertex Shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexId, shaderSource.get(ShaderType.VERTEX));
        glCompileShader(vertexId);

        int success = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexId, len));
            assert false : "";
        }

        // Fragment Shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentId, shaderSource.get(ShaderType.FRAGMENT));
        glCompileShader(fragmentId);

        success = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentId, len));
            assert false : "";
        }

        // Link shaders
        this.shaderProgramId = glCreateProgram();
        glAttachShader(this.shaderProgramId, vertexId);
        glAttachShader(this.shaderProgramId, fragmentId);
        glLinkProgram(this.shaderProgramId);

        success = glGetProgrami(this.shaderProgramId, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int len = glGetShaderi(this.shaderProgramId, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(this.shaderProgramId, len));
            assert false : "";
        }
    }

    public void use() {
        if (!beingUsed) {
            glUseProgram(shaderProgramId);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void setMat4f(String varName, Matrix4f mat4) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = mat4.get(stack.mallocFloat(16));
            use();
            glUniformMatrix4fv(getUniformLocation(varName), false, fb);
        }
    }

    public void setMat3f(String varName, Matrix3f mat3) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = mat3.get(stack.mallocFloat(9));
            use();
            glUniformMatrix3fv(getUniformLocation(varName), false, fb);
        }
    }

    public void setVec4f(String varName, Vector4f vec) {
        use();
        glUniform4f(getUniformLocation(varName), vec.x, vec.y, vec.z, vec.w);
    }

    public void setVec3f(String varName, Vector4f vec) {
        use();
        glUniform3f(getUniformLocation(varName), vec.x, vec.y, vec.z);
    }

    public void setVec2f(String varName, Vector4f vec) {
        use();
        glUniform2f(getUniformLocation(varName), vec.x, vec.y);
    }

    public void setFloat(String varName, float val) {
        use();
        glUniform1f(getUniformLocation(varName), val);
    }

    public void setInt(String varName, int val) {
        use();
        glUniform1i(getUniformLocation(varName), val);
    }

    private int getUniformLocation(String varName) {
        return glGetUniformLocation(this.shaderProgramId, varName);
    }
}
