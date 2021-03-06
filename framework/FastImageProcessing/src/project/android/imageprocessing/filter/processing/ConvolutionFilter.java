package project.android.imageprocessing.filter.processing;

import project.android.imageprocessing.filter.MultiPixelRenderer;
import android.opengl.GLES20;

/**
 * A basic convolution filter implementation of the MultiPixelRenderer.  
 * This class works for convolution filters of any size; however, if the size is an even number,
 * the filter will favour the bottom right.
 * @author Chris Batt
 */
public class ConvolutionFilter extends MultiPixelRenderer {
	protected static final String UNIFORM_FILTER = "u_Filter";
	private float[] filter;
	private int filterHandle;
	private String filterBody;
	private int filterSize;
	
	/**
	 * @param filter
	 * The convolution filter that should be applied to each pixel in the input texture.
	 * @param filterWidth
	 * The width of the convolution filter.
	 * @param filterHeight
	 * The height of the convolution filter.
	 */
	public ConvolutionFilter(float[] filter, int filterWidth, int filterHeight) {
		super();
		this.filter = filter;
		filterBody = createFilterBody(filterWidth, filterHeight);
		filterSize = filterWidth*filterHeight;
	}
	
	private String createFilterBody(int width, int height) {
		String filterBody = "   vec3 color = ";
		int middleWidth = (width-1)/2;
		int middleHeight = (height-1)/2;
		for(int j = 0; j < height; j++) {
			for(int i = 0; i < width; i++) {
				filterBody += "   texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+" + widthStep * " + (i-middleWidth) + ".0 + heightStep * " + (j-middleHeight) + ".0).rgb * "+UNIFORM_FILTER+"[" + (j*width+i) + "]";
				if(i == width-1 && j == height-1) {
					filterBody += ";\n";
				} else {
					filterBody += " +\n";
				}
			}
		}
		filterBody += "   gl_FragColor = vec4(color, 1);\n";//texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+").a);\n";
		return filterBody;
	}
	
	private int getFilterSize() {
		return filterSize;
	}
	
	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n" 
				+"uniform float "+UNIFORM_TEXELWIDTH+";\n"
				+"uniform float "+UNIFORM_TEXELHEIGHT+";\n" 
				+"uniform float "+UNIFORM_FILTER+"["+getFilterSize()+"];" 
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				
		  		+"void main(){\n"
				+"   vec2 widthStep = vec2("+UNIFORM_TEXELWIDTH+", 0);"
				+"   vec2 heightStep = vec2(0, "+UNIFORM_TEXELHEIGHT+");"
				+ filterBody
		  		+"}\n";		
	}
	
	@Override
	protected void initShaderHandles() {
		super.initShaderHandles();
		filterHandle = GLES20.glGetUniformLocation(programHandle, UNIFORM_FILTER);
	}

	@Override
	protected void passShaderValues() {
		super.passShaderValues();
		GLES20.glUniform1fv(filterHandle, filterSize, filter, 0);
	}
}
