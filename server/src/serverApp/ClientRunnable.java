package serverApp;

import communication.Request;
import communication.RequestType;
import javafx.scene.control.TextArea;
import serverCommunicationHandlers.LoginRequestHandler;
import serverCommunicationHandlers.RequestHandler;
import serverCommunicationHandlers.RequestHandlerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientRunnable implements Runnable {
    private final Socket connection;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;
    private final ServerMessageLogger logger;
    private String clientName;
    private RequestHandlerFactory factory;

    ClientRunnable(Socket connect, TextArea textArea) throws IOException {
        connection = connect;
        setClientName("guest");

        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());

        logger = new ServerMessageLogger(textArea);
        initialiseFactory();
    }

    private void initialiseFactory() {
        factory = new RequestHandlerFactory(null, outputStream,
                clientName);
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @Override
    public void run() {
        logger.displayMessage("Connection received from: " + connection.getInetAddress().getHostName());

        try {
            processConnection();
        } catch (IOException ioException) {
            logger.displayMessage(String.format("%s has terminated the connection.", clientName));
        } finally {
            closeConnection();
        }
    }

    private void processConnection() throws IOException {
        logger.displayMessage("Server connected successfully.");

        while (true) {
            processClientRequest();
        }
    }

    private void processClientRequest() throws IOException {
        try {
            Object clientRequest = inputStream.readObject();

            if (clientRequest instanceof String) {
                displayMessageOnTextLog((String) clientRequest);
            } else {
                processRequest((Request) clientRequest);
            }

        } catch (ClassNotFoundException classNotFoundException) {
            logger.displayMessage(String.format("Error: Unknown object received from %s.", clientName));
            classNotFoundException.printStackTrace();
        }
    }

    private void displayMessageOnTextLog(String message) {
        logger.displayMessage(message);
    }

    private void processRequest(Request clientRequest) throws IOException {
        factory.setClientRequest(clientRequest);
        RequestHandler processor = factory.createRequestProcessor(clientRequest.getType());
        processor.processRequest();

        if (loginIsSuccessful(clientRequest.getType(), processor)) {
            factory.setClientName(processor.getClientName());
            setClientName(processor.getClientName());
        }
    }

    private boolean loginIsSuccessful(RequestType requestType,
                                      RequestHandler requestHandler) {
        //if the request isn't for a login, the first boolean will return false and end the check
        //so we won't need to fear improper casting from derived RequestProcessor classes
        return isLoginRequest(requestType) && isSuccessful(requestHandler);
    }

    private boolean isLoginRequest(RequestType requestType) {
        return requestType == RequestType.LOGIN;
    }

    private boolean isSuccessful(RequestHandler requestHandler) {
        return ((LoginRequestHandler) requestHandler).isSuccessfulRequest();
    }

    private void closeConnection() {
        try {
            closeInputStream();
            closeOutputStream();
            closeConnectionSocket();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void closeInputStream() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    private void closeOutputStream() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
    }

    private void closeConnectionSocket() throws IOException {
        if (connection != null) {
            connection.close();
        }
    }
}