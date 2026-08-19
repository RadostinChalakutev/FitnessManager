package com.fitness.database;

import com.fitness.model.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {

    public void save(Member member, int subscriptionId) {

        String sql = """
                INSERT INTO members (
                    first_name,
                    last_name,
                    phone,
                    egn,
                    email,
                    email_verified,
                    verification_token,
                    subscription_id,
                    start_date,
                    end_date,
                    payment_method,
                    amount
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, member.getFirstName());
            statement.setString(2, member.getLastName());
            statement.setString(3, member.getPhone());
            statement.setString(4, member.getEgn());
            statement.setString(5, member.getEmail());
            statement.setBoolean(6, member.isEmailVerified());
            statement.setString(7, member.getVerificationToken());
            statement.setInt(8, subscriptionId);
            statement.setString(9, member.getStartDate().toString());
            statement.setString(10, member.getEndDate().toString());
            statement.setString(11, member.getPaymentMethod());
            statement.setDouble(12, member.getAmount());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Member> findAll() {

        List<Member> members = new ArrayList<>();

        String sql = "SELECT * FROM members";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {

                Member member = new Member(
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("phone"),
                        resultSet.getString("egn"),
                        resultSet.getString("email"),
                        resultSet.getString("subscription_id"),
                        java.time.LocalDate.parse(
                                resultSet.getString("start_date")
                        ),
                        java.time.LocalDate.parse(
                                resultSet.getString("end_date")
                        ),
                        resultSet.getString("payment_method"),
                        resultSet.getDouble("amount")
                );

                member.setId(
                        resultSet.getInt("id")
                );

                member.setEmailVerified(
                        resultSet.getBoolean("email_verified")
                );

                member.setVerificationToken(
                        resultSet.getString("verification_token")
                );

                members.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    public boolean update(
            Member member,
            int subscriptionId) {

        String sql = """
            UPDATE members
            SET first_name = ?,
                last_name = ?,
                phone = ?,
                egn = ?,
                email = ?,
                subscription_id = ?,
                start_date = ?,
                end_date = ?,
                payment_method = ?,
                amount = ?
            WHERE id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, member.getFirstName());
            statement.setString(2, member.getLastName());
            statement.setString(3, member.getPhone());
            statement.setString(4, member.getEgn());
            statement.setString(5, member.getEmail());
            statement.setInt(6, subscriptionId);
            statement.setString(7, member.getStartDate().toString());
            statement.setString(8, member.getEndDate().toString());
            statement.setString(9, member.getPaymentMethod());
            statement.setDouble(10, member.getAmount());
            statement.setInt(11, member.getId());

            int rowsUpdated =
                    statement.executeUpdate();

            System.out.println(
                    "Updated rows: " + rowsUpdated
            );

            return rowsUpdated > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Member update failed."
            );

            e.printStackTrace();

            return false;
        }
    }

    public Member findByVerificationToken(
            String token) {

        String sql = """
                SELECT * FROM members
                WHERE verification_token = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, token);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    Member member = new Member(
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("phone"),
                            resultSet.getString("egn"),
                            resultSet.getString("email"),
                            resultSet.getString("subscription_id"),
                            java.time.LocalDate.parse(
                                    resultSet.getString("start_date")
                            ),
                            java.time.LocalDate.parse(
                                    resultSet.getString("end_date")
                            ),
                            resultSet.getString("payment_method"),
                            resultSet.getDouble("amount")
                    );

                    member.setId(
                            resultSet.getInt("id")
                    );

                    member.setEmailVerified(
                            resultSet.getBoolean("email_verified")
                    );

                    member.setVerificationToken(
                            resultSet.getString(
                                    "verification_token"
                            )
                    );

                    return member;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void verifyEmail(int memberId) {

        String sql = """
                UPDATE members
                SET email_verified = true,
                    verification_token = NULL
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, memberId);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updatePersonalData(Member member) {

        String sql = """
            UPDATE members
            SET first_name = ?,
                last_name = ?,
                phone = ?,
                egn = ?,
                email = ?
            WHERE id = ?
            """;

        try (Connection connection =
                     DatabaseConnection.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, member.getFirstName());
            statement.setString(2, member.getLastName());
            statement.setString(3, member.getPhone());
            statement.setString(4, member.getEgn());
            statement.setString(5, member.getEmail());
            statement.setInt(6, member.getId());

            int rowsUpdated =
                    statement.executeUpdate();

            System.out.println(
                    "Updated personal data: "
                            + rowsUpdated
            );

            return rowsUpdated > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Failed to update member."
            );

            e.printStackTrace();

            return false;
        }
    }
}