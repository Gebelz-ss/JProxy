package net.orange.jproxy.render.shader;

import org.lwjgl.opengl.GL46;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;

import net.orange.jproxy.resource.JProxyResource;

public class BackgroundRenderer {

	private ShaderProgram shaderProgram;
	private Mesh mesh;

	public BackgroundRenderer() {
		shaderProgram = ShaderUtil.loadShader(new JProxyResource("shaders/backgroundVertex.vs"),
				new JProxyResource("shaders/impl/background.frag"));
		mesh = new Mesh(true, 4, 0, new VertexAttribute(VertexAttributes.Usage.Position, 2, "aPosition"));
		final float[] vertices = { -1, -1, -1, 1, 1, 1, 1, -1 };
		mesh.setVertices(vertices);
	}

	public void render(final float time, final Vector2 mousePosition) {
		ShaderUtil.useShader(shaderProgram);
		shaderProgram.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		shaderProgram.setUniformf("time", time);
		mesh.render(shaderProgram, GL46.GL_TRIANGLE_FAN);
	}
}