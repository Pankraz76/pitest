package org.pitest.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

class SocketReadingCallable implements Callable<ExitCode> {

  private final Consumer<SafeDataOutputStream> sendInitialData;
  private final ReceiveStrategy                   receive;
  private final ServerSocket                      socket;

  SocketReadingCallable(final ServerSocket socket,
      final Consumer<SafeDataOutputStream> sendInitialData,
      final ReceiveStrategy receive) {
    this.socket = socket;
    this.sendInitialData = sendInitialData;
    this.receive = receive;
  }

  @Override
  public ExitCode call() throws Exception {
    try (var clientSocket = this.socket.accept()) {
      try (var bif = new BufferedInputStream(
          clientSocket.getInputStream())) {

        sendDataToMinion(clientSocket);

        final var is = new SafeDataInputStream(bif);
        return receiveResults(is);
      } catch (final IOException e) {
        throw Unchecked.translateCheckedException(e);
      }
    } finally {
      try {
        this.socket.close();
      } catch (final IOException e) {
        throw Unchecked.translateCheckedException(e);
      }
    }
  }

  private void sendDataToMinion(final Socket clientSocket) throws IOException {
    final var os = clientSocket.getOutputStream();
    final var dos = new SafeDataOutputStream(os);
    this.sendInitialData.accept(dos);
  }

  private ExitCode receiveResults(final SafeDataInputStream is) {
    byte control = is.readByte();
    while (control != Id.DONE) {
      this.receive.apply(control, is);
      control = is.readByte();
    }
    return ExitCode.fromCode(is.readInt());

  }

}