package com.rhem;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.rhem.atmosphere.BaseEvent;
import com.rhem.atmosphere.EventBar;
import com.rhem.atmosphere.RPCSerializer;
import org.atmosphere.gwt20.client.*;

import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

    private DialogBox dialogBox;
    HTML serverResponseLabel;
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//atmos
		RPCSerializer rpc_serializer = GWT.create(RPCSerializer.class);

		final AtmosphereRequestConfig rpcRequestConfig = AtmosphereRequestConfig.create(rpc_serializer);
		rpcRequestConfig.setUrl(GWT.getModuleBaseURL() + "atmosphere/rpc");
		rpcRequestConfig.setTransport(AtmosphereRequestConfig.Transport.STREAMING);
		rpcRequestConfig.setFallbackTransport(AtmosphereRequestConfig.Transport.LONG_POLLING);
		rpcRequestConfig.setOpenHandler(new AtmosphereOpenHandler() {
			@Override
			public void onOpen(AtmosphereResponse response) {
				//logger.info("RPC Connection opened");
				//System.out.println("RPC Connection opened");
			}
		});
		rpcRequestConfig.setReopenHandler(new AtmosphereReopenHandler() {
			@Override
			public void onReopen(AtmosphereResponse response) {
				//logger.info("RPC Connection reopened");
				//System.out.println("RPC Connection reopened");
			}
		});
		rpcRequestConfig.setCloseHandler(new AtmosphereCloseHandler() {
			@Override
			public void onClose(AtmosphereResponse response) {
				//logger.info("RPC Connection closed");
				//System.out.println("RPC Connection closed");
			}
		});
		rpcRequestConfig.setMessageHandler(new AtmosphereMessageHandler() {
			@Override
			public void onMessage(AtmosphereResponse response) {
				List<BaseEvent> messages = response.getMessages();
				for (BaseEvent event : messages) {
					//logger.info("received message through RPC: " + event.toString());
					//System.out.println("received message through RPC: " + event.toString());
                    dialogBox.setText("received message through RPC:");
                    serverResponseLabel.setText(event.toString());
                    dialogBox.center();
				}
			}
		});

		final Atmosphere atmosphere = Atmosphere.create();
		final AtmosphereRequest rpcRequest = atmosphere.subscribe(rpcRequestConfig);
		//atmos

		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("GWT User");
		final Label errorLabel = new Label();

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

		// Add the nameField and sendButton to the RootPanel
		// Use RootPanel.get() to get the entire body element
		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);

		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
		dialogVPanel.add(textToServerLabel);
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});

        greetingService.greetServer("hola",
                new AsyncCallback<GreetingResponse>() {
                    public void onFailure(Throwable caught) {
                        // Show the RPC error message to the user
                        dialogBox
                                .setText("Remote Procedure Call - Failure");
                        serverResponseLabel
                                .addStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML(SERVER_ERROR);
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }

                    public void onSuccess(GreetingResponse result) {
                        dialogBox.setText("Remote Procedure Call");
                        serverResponseLabel
                                .removeStyleName("serverResponseLabelError");
                        serverResponseLabel.setHTML(new SafeHtmlBuilder()
                                .appendEscaped(result.getGreeting())
                                .appendHtmlConstant("<br><br>I am running ")
                                .appendEscaped(result.getServerInfo())
                                .appendHtmlConstant(".<br><br>It looks like you are using:<br>")
                                .appendEscaped(result.getUserAgent())
                                .toSafeHtml());
                        dialogBox.center();
                        closeButton.setFocus(true);
                    }
                });
		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler, KeyUpHandler {
			/**
			 * Fired when the user clicks on the sendButton.
			 */
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}

			/**
			 * Fired when the user types in the nameField.
			 */
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					sendNameToServer();
				}
			}

			/**
			 * Send the name from the nameField to the server and wait for a response.
			 */
			private void sendNameToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter at least four characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				/*greetingService.greetServer(textToServer,
						new AsyncCallback<GreetingResponse>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox
										.setText("Remote Procedure Call - Failure");
								serverResponseLabel
										.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(GreetingResponse result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel
										.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(new SafeHtmlBuilder()
										.appendEscaped(result.getGreeting())
										.appendHtmlConstant("<br><br>I am running ")
										.appendEscaped(result.getServerInfo())
										.appendHtmlConstant(".<br><br>It looks like you are using:<br>")
										.appendEscaped(result.getUserAgent())
										.toSafeHtml());
								dialogBox.center();
								closeButton.setFocus(true);
							}
						});*/
                //atmos
                final AtmosphereRequest rpcRequest = atmosphere.subscribe(rpcRequestConfig);
                EventBar be = new EventBar();
                be.setData1("hola1");
                be.setData2("hola2");

                try {
                    rpcRequest.push(be);
                } catch (SerializationException e) {
                    e.printStackTrace();
                }

            }
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		nameField.addKeyUpHandler(handler);
	}
}
