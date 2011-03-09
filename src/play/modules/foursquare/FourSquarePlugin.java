package play.modules.foursquare;

import javax.naming.ConfigurationException;

import play.Play;
import play.PlayPlugin;
import play.exceptions.UnexpectedException;
import play.mvc.Router;

/**
 * The Class FourSquarePlugin.
 * 
 * @author Felipe Oliveira (http://geeks.aretotally.in)
 * @copyright Felipe Oliveira (http://geeks.aretotally.in)
 */
public class FourSquarePlugin extends PlayPlugin {
    
    /**
     * Start
     * 
     * @see play.PlayPlugin#onApplicationStart()
     */
    @Override
    public void onApplicationStart() {
    	// Do Nothing
    }
    
    /**
     * Routes
     * 
     * @see play.PlayPlugin#onRoutesLoaded()
     */
    @Override
    public void onRoutesLoaded() {
        Router.addRoute("GET", "/foursquare/login", "FourSquareController.login");
        Router.addRoute("GET", "/foursquare/logout", "FourSquareController.logout");
        Router.addRoute("GET", "/foursquare/oauth/callback", "FourSquareController.callback");
    }
    
}
