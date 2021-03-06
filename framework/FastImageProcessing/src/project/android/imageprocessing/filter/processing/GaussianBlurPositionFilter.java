package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.CompositeFilter;
import project.android.imageprocessing.input.GLTextureOutputRenderer;
import android.graphics.PointF;
import android.opengl.GLES20;

/**
 * The inverse of the {@link GaussianSelectiveBlurFilter}, applying the blur only within a certain circle
 * blurSize: A multiplier for the size of the blur, ranging from 0.0 on up
 * blurCenter: Center for the blur
 * blurRadius: Radius for the blur
 * @author Chris Batt
 */
public class GaussianBlurPositionFilter extends CompositeFilter {
	protected static final String UNIFORM_BLUR_SIZE = "u_BlurSize";
	protected static final String UNIFORM_ASPECT_RATIO = "u_AspectRatio";
	protected static final String UNIFORM_EXCLUDE_CIRCLE_POINT = "u_ExcludeCirclePoint";
	protected static final String UNIFORM_EXCLUDE_CIRCLE_RADIUS = "u_ExcludeCircleRadius";
	
	private float blurSize;
	private float aspectRatio;
	private PointF excludedCirclePoint;
	private float excludedCircleRadius;
	private int blurSizeHandle;
	private int aspectRatioHandle;
	private int excludedCirclePointHandle;
	private int excludedCircleRadiusHandle;
	
	private GaussianBlurFilter blur;
	
	public GaussianBlurPositionFilter(float blurSize, float aspectRatio, PointF excludedCirclePoint, float excludedCircleRadius, float excludedBlurSize) {
		super(2);
		this.blurSize = excludedBlurSize;
		this.aspectRatio = aspectRatio;
		this.excludedCirclePoint = excludedCirclePoint;
		this.excludedCircleRadius = excludedCircleRadius;
		
		blur = new GaussianBlurFilter(blurSize);
		blur.addTarget(this);

		registerInitialFilter(blur);
		registerTerminalFilter(blur);
	}
	
	@Override
	protected String getFragmentShader() {
		return
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n" 
				+"uniform sampler2D "+UNIFORM_TEXTUREBASE+1+";\n"
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				+"uniform float "+UNIFORM_BLUR_SIZE+";\n"
				+"uniform float "+UNIFORM_ASPECT_RATIO+";\n"
				+"uniform vec2 "+UNIFORM_EXCLUDE_CIRCLE_POINT+";\n"
				+"uniform float "+UNIFORM_EXCLUDE_CIRCLE_RADIUS+";\n"
						
				
		  		+"void main(){\n"
				+"   vec4 sharpImageColor = texture2D("+UNIFORM_TEXTURE0+", "+VARYING_TEXCOORD+");\n"
				+"   vec4 blurredImageColor = texture2D("+UNIFORM_TEXTUREBASE+1+", "+VARYING_TEXCOORD+");\n"
				+"   vec2 texCoordAfterAspect = vec2("+VARYING_TEXCOORD+".x, "+VARYING_TEXCOORD+".y * "+UNIFORM_ASPECT_RATIO+" + 0.5 - 0.5 * "+UNIFORM_ASPECT_RATIO+");\n"
		  		+"   float distanceFromCenter = distance("+UNIFORM_EXCLUDE_CIRCLE_POINT+", texCoordAfterAspect);\n"
				+"   gl_FragColor = mix(blurredImageColor, sharpImageColor, smoothstep("+UNIFORM_EXCLUDE_CIRCLE_RADIUS+" - "+UNIFORM_BLUR_SIZE+", "+UNIFORM_EXCLUDE_CIRCLE_RADIUS+", distanceFromCenter));\n"
		  		+"}\n";
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		blurSizeHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_BLUR_SIZE);
		aspectRatioHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_ASPECT_RATIO);
		excludedCirclePointHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_EXCLUDE_CIRCLE_POINT);
		excludedCircleRadiusHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_EXCLUDE_CIRCLE_RADIUS);
	} 
	
	@Override
	public void newTextureReady(int texture, GLTextureOutputRenderer source) {
		if(filterLocations.size() < 2 || !filterLocations.contains(source)) {
			clearRegisteredFilterLocations();
			registerFilterLocation(source, 0);
			registerFilterLocation(blur, 1);
			registerInputOutputFilter(source);
		}
		super.newTextureReady(texture, source);
	}
	
	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1f(blurSizeHandle, blurSize);
		GLES20.glUniform1f(aspectRatioHandle, aspectRatio);
		GLES20.glUniform1f(excludedCircleRadiusHandle, excludedCircleRadius);
		GLES20.glUniform2f(excludedCirclePointHandle, excludedCirclePoint.x, excludedCirclePoint.y);
	}
}
