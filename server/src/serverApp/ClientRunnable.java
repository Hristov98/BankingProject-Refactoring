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
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String clientName;
    private UserLoader userLoader;
    private BankCardTableController cardController;
    private ServerMessageLogger logger;

    ClientRunnable(Socket connect) {
        connection = connect;
        setClientName("guest");
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public void initialiseUserLoader(UserLoader userLoader) {
        this.userLoader = userLoader;
    }

    public void initialiseCardController(BankCardTableController cardController) {
        this.cardController = cardController;
    }

    public void initialiseLogger(TextArea textArea) {
        logger = new ServerMessageLogger(textArea);
    }

    @Override
    public void run() {
        logger.displayMessage("Connection received from: " + connection.getInetAddress().getHostName());

        try {
            initialiseStreams();
            processConnection();
        } catch (IOException ioException) {
            logger.displayMessage(String.format("%s has terminated the connection.", clientName));
        } finally {
            closeConnection();
        }
    }

    private void initialiseStreams() throws IOException {
        initialiseInputStream();
        initialiseOutputStream();
        logger.displayMessage("Server Input/Output streams loaded successfully.");
    }

    private void initialiseInputStream() throws IOException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
    }

    private void initialiseOutputStream() throws IOException {
        inputStream = new ObjectInputStream(connection.getInputStream());
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
                RequestProcessor processor = new LoginRequestProcessor((Request) clientRequest);
                initialiseProcessor(processor);
                processor.processRequest();
                setClientName(processor.getClientName());
            }
            if (clientRequest instanceof EncryptionRequest) {
                CardRequestProcessor processor = new EncryptionRequestProcessor((Request) clientRequest);
                initialiseCardProcessor(processor);
                processor.processRequest();
            }
            if (clientRequest instanceof DecryptionRequest) {
                CardRequestProcessor processor = new DecryptionRequestProcessor((Request) clientRequest);
                initialiseCardProcessor(processor);
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

    private void initialiseProcessor(RequestProcessor processor) {
        processor.setClientName(clientName);
        processor.initialiseOutputStream(outputStream);
        processor.initialiseUserLoader(userLoader);
        processor.initialiseLogger(logger);
    }

    private void initialiseCardProcessor(CardRequestProcessor processor) {
        initialiseProcessor(processor);
        processor.initialiseCardController(cardController);
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