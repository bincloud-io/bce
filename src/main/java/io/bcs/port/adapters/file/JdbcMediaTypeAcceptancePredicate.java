package io.bcs.port.adapters.file;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import javax.sql.DataSource;

import io.bcs.DictionaryValidation.DictionaryPredicate;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class JdbcMediaTypeAcceptancePredicate implements DictionaryPredicate<String> {
    private static final String CHECK_ACCEPTANCE_QUERY = "SELECT COUNT(*) > 0 "
            + "AS IS_EXISTS FROM REF_MEDIA_TYPES MT WHERE MT.MEDIA_TYPE = ?";
    private final DataSource dataSource;

    @Override
    @SneakyThrows
    public boolean isSatisfiedBy(String mediaType) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(CHECK_ACCEPTANCE_QUERY);
            statement.setString(1, mediaType);
            return Optional.of(statement.executeQuery()).filter(this::isDataReceived).map(this::extractResult)
                    .orElse(true);
        }
    }

    @SneakyThrows
    private boolean isDataReceived(ResultSet resultSet) {
        return resultSet.next();
    }

    @SneakyThrows
    private boolean extractResult(ResultSet resultSet) {
        return resultSet.getBoolean(1);
    }
}
