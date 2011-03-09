package controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FoursquareApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import play.Logger;
import play.Play;
import play.cache.Cache;
import play.exceptions.UnexpectedException;
import play.libs.XML;
import play.modules.foursquare.FourSquareProfile;
import play.mvc.Controller;
import play.mvc.Router;

// TODO: Auto-generated Javadoc
/**
 * The Class FourSquareController.
 * 
 * @author Felipe Oliveira (http://geeks.aretotally.in)
 * @copyright Felipe Oliveira (http://geeks.aretotally.in)
 */
public class FourSquareController extends Controller {

	/**
	 * Gets the api key.
	 * 
	 * @return the api key
	 */
	public static String getApiKey() {
		return Play.configuration.getProperty("foursquare.apiKey");
	}

	/**
	 * Gets the secret.
	 * 
	 * @return the secret
	 */
	public static String getSecret() {
		return Play.configuration.getProperty("foursquare.secret");
	}

	/**
	 * Gets the model.
	 * 
	 * @return the model
	 */
	public static String getModel() {
		return Play.configuration.getProperty("foursquare.model");
	}

	/**
	 * Gets the land url.
	 * 
	 * @return the land url
	 */
	public static String getLandUrl() {
		return Play.configuration.getProperty("foursquare.landUrl");
	}

	/**
	 * Gets the login url.
	 * 
	 * @return the login url
	 */
	public static String getLoginUrl() {
		OAuthService service = getService();
		Token token = service.getRequestToken();
		Logger.info("Request Token - " + token.getToken() + " with secret "
				+ token.getSecret());
		Cache.add(token.getToken(), token.getSecret());
		return service.getAuthorizationUrl(token);
	}

	/**
	 * Login.
	 */
	public static void login() {
		String url = getLoginUrl();
		Logger.info("Redirect: " + url);
		redirect(url);
	}

	/**
	 * Gets the service.
	 * 
	 * @return the service
	 */
	private static OAuthService getService() {
		return new ServiceBuilder().provider(FoursquareApi.class)
				.apiKey(getApiKey()).apiSecret(getSecret())
				.callback(Router.getFullUrl("FourSquareController.callback"))
				.build();
	}

	/**
	 * Gets the service no callback.
	 * 
	 * @return the service no callback
	 */
	private static OAuthService getServiceNoCallback() {
		return new ServiceBuilder().provider(FoursquareApi.class)
				.apiKey(getApiKey()).apiSecret(getSecret()).build();
	}

	/**
	 * Callback.
	 */
	public static void callback() {
		// Get Service
		OAuthService service = getServiceNoCallback();

		// Get Query String Token and Verifier
		Logger.info("FourSquare Callback - Params: " + params);
		String oauthToken = params.get("oauth_token");
		String oauthVerifier = params.get("oauth_verifier");
		Verifier verifier = new Verifier(oauthVerifier);
		Logger.info("Token: " + oauthToken);
		Logger.info("Verifier: " + oauthVerifier);

		// Request Access Token
		Token accessToken = service.getAccessToken(
				new Token(oauthToken, Cache.get(oauthToken, String.class)),
				verifier);

		// Log Debug
		Logger.info("FourSquare Access Token: " + accessToken);

		// Check Response
		if (accessToken != null && accessToken.getToken() != null) {
			// Get Profile Details
			String url = "http://api.foursquare.com/v1/people/~:(id,first-name,last-name,industry,picture-url,headline)";
			OAuthRequest request = new OAuthRequest(Verb.GET, url);
			service.signRequest(accessToken, request);
			Response response = request.send();
			String responseBody = response.getBody();
			Logger.info("Response Body: %s", responseBody);

			// Check Status
			if (response == null || response.getCode() != 200) {
				String msg = "";
				if (response != null) {
					msg = response.getBody();
				}
				throw new UnexpectedException(msg);
			}

			// Parse XML Response
			Map<String, String> data = new HashMap<String, String>();
			Document doc = XML.getDocument(responseBody);
			Node person = doc.getElementsByTagName("person").item(0);
			NodeList list = person.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				data.put(n.getNodeName(), n.getTextContent());
			}

			// Load Data Object
			FourSquareProfile p = new FourSquareProfile(data.get("id"),
					data.get("first-name"), data.get("last-name"),
					data.get("industry"), data.get("picture-url"),
					data.get("headline"), accessToken.getToken());

			// Log Debug
			Logger.info("Profile Data Map: %s", data);
			Logger.info("Profile Data Object: %s", p);

			// Do Callback to Calling App
			try {
				Class model = Class.forName(getModel());
				Method method = model.getMethod("foursquareOAuthCallback",
						new Class[] { FourSquareProfile.class });
				if (Modifier.isStatic(method.getModifiers())) {
					method.invoke(null, p);

				} else {
					throw new UnexpectedException(
							"Method foursquareOAuthCallback method needs to be static");
				}
			} catch (ClassNotFoundException e) {
				throw new UnexpectedException("Cannot find your model class "
						+ getModel());

			} catch (NoSuchMethodException e) {
				throw new UnexpectedException(
						"Model class "
								+ getModel()
								+ " must provide a method with this signature: [public static void foursquareOAuthCallback(play.modules.foursquare.FourSquareProfile o)]");

			} catch (IllegalAccessException e) {
				throw new UnexpectedException(
						"Module foursquare does not have access to call your model's foursquareOAuthCallback(play.modules.foursquare.FourSquareProfile o)");

			} catch (InvocationTargetException e) {
				throw new UnexpectedException(
						"Module foursquare encountered an error while calling your model's foursquareOAuthCallback(play.modules.foursquare.FourSquareProfile o): "
								+ e.getMessage());
			}
		} else {
			throw new UnexpectedException("Access Token Unavailable");
		}

		// Redirect to Landing Page
		redirect(getLandUrl());
	}

	/**
	 * To String
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FourSquareController [getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}

}
