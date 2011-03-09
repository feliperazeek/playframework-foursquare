package tags.foursquare;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import play.mvc.Router;
import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;

// TODO: Auto-generated Javadoc
/**
 * The Class FourSquareTags.
 * 
 * @author Felipe Oliveira (http://geeks.aretotally.in)
 * @copyright Felipe Oliveira (http://geeks.aretotally.in)
 */
@FastTags.Namespace("foursquare")
public class FourSquareTags extends FastTags {

	/**
	 * FourSquare Login Tag
	 * 
	 * @param args
	 *            the args
	 * @param body
	 *            the body
	 * @param out
	 *            the out
	 * @param template
	 *            the template
	 * @param fromLine
	 *            the from line
	 */
	public static void _button(Map<?, ?> args, Closure body, PrintWriter out,
			ExecutableTemplate template, int fromLine) {
		Object labelArg = args.get("label");
		Object classArg = args.get("cssClass");
		String label = labelArg != null ? labelArg.toString()
				: "Login With Foursquare";
		String className = classArg != null ? classArg.toString()
				: "play-foursquare-button";
		out.println("<a href='"
				+ Router.getFullUrl("FourSquareController.login") + "' class='"
				+ className + "'>" + label + "</a>");
	}

}
