package com.fitness.server;

import com.fitness.database.MemberRepository;
import com.fitness.model.Member;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class VerificationServer {

    private final MemberRepository memberRepository =
            new MemberRepository();

    private HttpServer server;

    public void start() {

        try {

            server = HttpServer.create(
                    new InetSocketAddress(8080),
                    0
            );

            server.createContext(
                    "/verify",
                    this::handleVerification
            );

            server.setExecutor(null);

            server.start();

            System.out.println(
                    "Verification server started on port 8080."
            );

        } catch (IOException e) {

            System.out.println(
                    "Could not start verification server."
            );

            e.printStackTrace();
        }
    }

    private void handleVerification(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    405,
                    "Method Not Allowed"
            );

            return;
        }

        URI requestUri =
                exchange.getRequestURI();

        String query =
                requestUri.getQuery();

        if (query == null ||
                !query.startsWith("token=")) {

            sendResponse(
                    exchange,
                    400,
                    "Invalid verification link."
            );

            return;
        }

        String token =
                query.substring("token=".length());

        Member member =
                memberRepository
                        .findByVerificationToken(token);

        if (member == null) {

            sendResponse(
                    exchange,
                    404,
                    """
                    <html>
                    <body>
                        <h1>Invalid verification link</h1>
                        <p>The verification link is invalid or expired.</p>
                    </body>
                    </html>
                    """
            );

            return;
        }

        if (member.isEmailVerified()) {

            sendResponse(
                    exchange,
                    200,
                    """
                    <html>
                    <body>
                        <h1>Email already verified</h1>
                        <p>Your email address has already been verified.</p>
                    </body>
                    </html>
                    """
            );

            return;
        }

        memberRepository.verifyEmail(
                member.getId()
        );

        sendResponse(
                exchange,
                200,
                """
                <html>
                <body>
                    <h1>Email verified successfully!</h1>
                    <p>Your Fitness Manager email has been verified.</p>
                </body>
                </html>
                """
        );
    }

    private void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String message) throws IOException {

        byte[] response =
                message.getBytes();

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "text/html; charset=UTF-8"
                );

        exchange.sendResponseHeaders(
                statusCode,
                response.length
        );

        try (OutputStream outputStream =
                     exchange.getResponseBody()) {

            outputStream.write(response);
        }
    }
}