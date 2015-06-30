/*
 * This file is a part of the SchemaSpy project (http://schemaspy.sourceforge.net).
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011 John Currier
 *
 * SchemaSpy is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * SchemaSpy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sourceforge.schemaspy.view;

import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.sourceforge.schemaspy.Config;
import net.sourceforge.schemaspy.util.LineWriter;

/**
 * The page that contains links to the various schemas that were analyzed
 *
 * @author John Currier
 */
public class HtmlMultipleSchemasIndexPage extends HtmlFormatter {
    private static HtmlMultipleSchemasIndexPage instance = new HtmlMultipleSchemasIndexPage();

    /**
     * Singleton: Don't allow instantiation
     */
    private HtmlMultipleSchemasIndexPage() {
    }

    /**
     * Singleton accessor
     *
     * @return the singleton instance
     */
    public static HtmlMultipleSchemasIndexPage getInstance() {
        return instance;
    }

    public void write(String dbName, List<String> populatedSchemas, DatabaseMetaData meta, LineWriter index) throws IOException {
        writeHeader(dbName, meta, populatedSchemas.size(), false, populatedSchemas.get(0).toString(), index);

        for (String schema : populatedSchemas) {
            writeLineItem(schema, index);
        }

        writeFooter(index);
    }

    private void writeHeader(String databaseName, DatabaseMetaData meta, int numberOfSchemas, boolean showIds, String aSchema, LineWriter html) throws IOException {
        String connectTime = new SimpleDateFormat("EEE MMM dd HH:mm z yyyy").format(new Date());

        html.writeln("<!DOCTYPE html>");
        html.writeln("<!--[if IE 9]><html class=\"lt-ie10\" lang=\"en\" > <![endif]-->");
        html.writeln("<html>");
        html.writeln("<head>");
        html.write("  <title>Database Analysis");
        if (databaseName != null) {
            html.write(" of Database ");
            html.write(databaseName);
        }
        html.writeln("</title>");

        html.write("  <link rel=stylesheet href='");
        html.write(aSchema);
        html.writeln("/schemaSpy.css' type='text/css'>");

        html.write("  <link rel=stylesheet href='");
        html.write(aSchema);
        html.writeln("/schemaSpy-print.css' type='text/css'>");

        html.write("  <link rel=stylesheet href='");
        html.write(aSchema);
        html.writeln("/css/normalize.css' type='text/css'>");

        html.write("  <link rel=stylesheet href='");
        html.write(aSchema);
        html.writeln("/css/foundation.css' type='text/css'>");

        html.writeln("  <meta HTTP-EQUIV='Content-Type' CONTENT='text/html; charset=" + Config.getInstance().getCharset() + "'>");
        html.writeln("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.writeln("</head>");
        html.writeln("<body>");
        writeTableOfContents(html);
        html.writeln("<div class='inner-wrap'>");
        html.writeln("<div");
        html.writeln("<div class=\"header\">");
        html.write("<h2>");
        html.write("Analysis");
        if (databaseName != null) {
            html.write(" of Database ");
            html.write(databaseName);
        }
        html.writeln("</h2>");

        html.writeln("<table width='100%'>");
        html.writeln(" <tr><td class='container'>");
        writeGeneratedOn(connectTime, html);
        html.writeln(" </td></tr>");
        html.writeln(" <tr>");
        html.write("  <td class='container'>");
        if (meta != null) {
            html.write("Database Type: ");
            html.write(getDatabaseProduct(meta));
        }
        html.writeln("  </td>");
        html.writeln(" </tr>");
        html.writeln("</table>");

        html.writeln("<div class='indent'>");
        html.write("<b>");
        html.write(String.valueOf(numberOfSchemas));
        if (databaseName != null)
            html.write(" Schema");
        else
            html.write(" Database");
        html.write(numberOfSchemas == 1 ? "" : "s");
        html.writeln(":</b>");
        html.writeln("<table class='dataTable' rules='groups'>");
        html.writeln("<colgroup>");
        html.writeln("<thead align='left'>");
        html.writeln("<tr>");
        html.write("  <th valign='bottom'>");
        if (databaseName != null)
            html.write("Schema");
        else
            html.write("Database");
        html.writeln("</th>");
        if (showIds)
            html.writeln("  <th align='center' valign='bottom'>ID</th>");
        html.writeln("</tr>");
        html.writeln("</thead>");
        html.writeln("<tbody>");
    }

    private void writeLineItem(String schema, LineWriter index) throws IOException {
        index.writeln(" <tr>");
        index.write("  <td class='detail'><a href='");
        index.write(schema);
        index.write("/index.html'>");
        index.write(schema);
        index.writeln("</a></td>");
        index.writeln(" </tr>");
    }

    @Override
    protected void writeTableOfContents(LineWriter html) throws IOException {
        // have to use a table to deal with a horizontal scrollbar showing up inappropriately
        html.writeln("<nav class=\"top-bar\" data-topbar role=\"navigation\">");
        html.writeln("<section class=\"top-bar-section\">");
        html.writeln(" <ul class=\"left\">");
        html.writeln("  <li class='active'><a href='index.html' title='All user schemas in the database'>Schemas</a></li>");
        html.writeln(" </ul>");
        html.writeln("</section>");
        html.writeln("</nav>");
    }

    @Override
    protected void writeFooter(LineWriter html) throws IOException {
        html.writeln("</tbody>");
        html.writeln("</table>");
        super.writeFooter(html);
    }

    /**
     * Copy / paste from Database, but we can't use Database here...
     *
     * @param meta DatabaseMetaData
     * @return String
     */
    private String getDatabaseProduct(DatabaseMetaData meta) {
        try {
            return meta.getDatabaseProductName() + " - " + meta.getDatabaseProductVersion();
        } catch (SQLException exc) {
            return "";
        }
    }
}
