package project.android.imageprocessing.filter.blend;

import project.android.imageprocessing.filter.MultiInputFilter;

/**
 * Applies a overlay blend of two images
 * @author Chris Batt
 */
public class OverlayBlendFilter extends MultiInputFilter {
	public OverlayBlendFilter() {
		super(2);
	}
	
	@Override
	protected String getFragmentShader() {
		return 
				 "precision mediump float;\n" 
				+"uniform sampler2D "+UNIFORM_TEXTURE0+";\n"  
				+"uniform sampler2D "+UNIFORM_TEXTUREBASE+1+";\n" 
				+"varying vec2 "+VARYING_TEXCOORD+";\n"	
				
		  		+"void main(){\n"
		  		+"   vec4 color1 = texture2D("+UNIFORM_TEXTURE0+","+VARYING_TEXCOORD+");\n"
		  		+"   vec4 color2 = texture2D("+UNIFORM_TEXTUREBASE+1+","+VARYING_TEXCOORD+");\n"
		  		+"   float r;\n"
		  		+"   if (2.0 * color1.r < color1.a) {\n"
		  		+"     r = 2.0 * color2.r * color1.r + color2.r * (1.0 - color1.a) + color1.r * (1.0 - color2.a);\n"
		  		+"   } else {\n"
		  		+"     r = color2.a * color1.a - 2.0 * (color1.a - color1.r) * (color2.a - color2.r) + color2.r * (1.0 - color1.a) + color1.r * (1.0 - color2.a);\n"
	   			+"   }\n"
		  		+"   float g;\n"
		  		+"   if (2.0 * color1.g < color1.a) {\n"
		  		+"     g = 2.0 * color2.g * color1.g + color2.g * (1.0 - color1.a) + color1.g * (1.0 - color2.a);\n"
		  		+"   } else {\n"
		  		+"     g = color2.a * color1.a - 2.0 * (color1.a - color1.g) * (color2.a - color2.g) + color2.g * (1.0 - color1.a) + color1.g * (1.0 - color2.a);\n"
	   			+"   }\n"
		  		+"   float b;\n"
		  		+"   if (2.0 * color1.b < color1.a) {\n"
		  		+"     b = 2.0 * color2.b * color1.b + color2.b * (1.0 - color1.a) + color1.b * (1.0 - color2.a);\n"
		  		+"   } else {\n"
		  		+"     b = color2.a * color1.a - 2.0 * (color1.a - color1.b) * (color2.a - color2.b) + color2.b * (1.0 - color1.a) + color1.b * (1.0 - color2.a);\n"
	   			+"   }\n"
	   			+"   gl_FragColor = vec4(r, g, b, 1.0);\n"
		  		+"}\n";	
	}
}
