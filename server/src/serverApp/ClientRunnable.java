package serverApp;

import cardManipulation.BankCardTableController;
import communication.DecryptionRequest;
import communication.EncryptionRequest;
import communication.LoginRequest;
import communication.Request;
import javafx.scene.control.TextArea;
import serverCommunicationHandlers.*;
import userStorage.UserLoader;

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
    private UserLoader userLoader;
    private BankCardTableController cardController;

    ClientRunnable(Socket connect, UserLoader userLoader,
                   BankCardTableController cardController, TextArea textArea) throws IOException {
        connection = connect;
        setClientName("guest");
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        this.userLoader = userLoader;
        this.cardController = cardController;
        logger = new ServerMessageLogger(textArea);
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
                processString((String) clientRequest);
            }
            if (clientRequest instanceof LoginRequest) {
                RequestProcessor processor = new LoginRequestProcessor((Request) clientRequest,
                        userLoader, outputStream, logger, clientName);
                processor.processRequest();

                setClientName(processor.getClientName());
            }
            if (clientRequest instanceof EncryptionRequest) {
                CardRequestProcessor processor = new EncryptionRequestProcessor((Request) clientRequest,
                        userLoader, outputStream, logger, clientName, cardController);

                processor.processRequest();
            }
            if (clientRequest instanceof DecryptionRequest) {
                CardRequestProcessor processor = new DecryptionRequestProcessor((Request) clientRequest,
                        userLoader, outputStream, logger, clientName, cardController);

                processor.processRequest();
            }
        } catch (ClassNotFoundException classNotFoundException) {
            logger.displayMessage(String.format("Error: Unknown object received from %s.", clientName));
            classNotFoundException.printStackTrace();
        }
    }

    private void processString(String message) {
        logger.displayMessage(message);
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