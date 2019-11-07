package com.facebook.presto.plugin.clickhouse;

import com.facebook.presto.plugin.jdbc.BaseJdbcClient;
import com.facebook.presto.plugin.jdbc.BaseJdbcConfig;
import com.facebook.presto.plugin.jdbc.DriverConnectionFactory;
import com.facebook.presto.plugin.jdbc.JdbcColumnHandle;
import com.facebook.presto.plugin.jdbc.JdbcConnectorId;
import com.facebook.presto.plugin.jdbc.JdbcSplit;
import com.google.common.base.Joiner;
import io.airlift.log.Logger;
import ru.yandex.clickhouse.ClickHouseDriver;

import javax.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ClickhouseClient
        extends BaseJdbcClient
{
    private static final Joiner DOT_JOINER = Joiner.on(".");
    private static final Logger log = Logger.get(BaseJdbcClient.class);

    @Inject
    public ClickhouseClient(JdbcConnectorId connectorId, BaseJdbcConfig config)
    {
        super(connectorId, config, "\"", new DriverConnectionFactory(new ClickHouseDriver(), config));
        log.info("Creating a Clickhouse Client!");
    }

    @Override
    public PreparedStatement buildSql(Connection connection, JdbcSplit split, List<JdbcColumnHandle> columnHandles)
            throws SQLException
    {
        return new ClickhouseQueryBuilder(identifierQuote).buildSql(
                this,
                connection,
                split.getCatalogName(),
                split.getSchemaName(),
                split.getTableName(),
                columnHandles,
                split.getTupleDomain(),
                split.getAdditionalPredicate());
    }

    private static String singleQuote(String literal)
    {
        return "\'" + literal + "\'";
    }
}
