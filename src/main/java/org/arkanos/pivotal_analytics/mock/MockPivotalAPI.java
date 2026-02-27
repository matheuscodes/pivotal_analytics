/**
 *  Copyright (C) 2014 Matheus Borges Teixeira
 *  
 *  This file is part of Pivotal Analytics, a web tool for statistical
 *  observation and measurement of Pivotal Projects.
 *
 *  Pivotal Analytics is free software: you can redistribute it and/or 
 *  modify it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Pivotal Analytics.  If not, see <http://www.gnu.org/licenses/>
 */
package org.arkanos.pivotal_analytics.mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The {@code MockPivotalAPI} class simulates the Pivotal Tracker REST API v5.
 * It serves mocked project data so the application works without a real
 * Pivotal Tracker account.
 *
 * Handled endpoints (project ID is ignored and fixed demo data is returned):
 *   GET /services/v5/projects/{id}              - project metadata
 *   GET /services/v5/projects/{id}/memberships  - team members
 *   GET /services/v5/projects/{id}/iterations   - iteration history with stories
 *   GET /services/v5/projects/{id}/stories      - icebox (unscheduled) stories
 *
 * @version 1.0
 * @author Matheus Borges Teixeira
 */
@WebServlet("/services/v5/projects/*")
public class MockPivotalAPI extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Number of simulated iterations (2-week sprints from 2022-01-03) **/
    private static final int ITERATION_COUNT = 52;

    /** Iteration length in days **/
    private static final int ITERATION_DAYS = 14;

    /** Project start date **/
    private static final String PROJECT_START = "2022-01-03";

    /** Milliseconds per day **/
    private static final long DAY_MS = 24L * 60 * 60 * 1000;

    /** Project start as epoch ms (2022-01-03) **/
    private static final long PROJECT_START_MS = 1641168000000L;

    private static final SimpleDateFormat ISO_FMT;

    static {
        ISO_FMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        ISO_FMT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /** Mock team members **/
    private static final long[]   USER_IDS   = {1001L, 1002L, 1003L, 1004L, 1005L};
    private static final String[] USER_NAMES = {
        "Alice Johnson", "Bob Smith", "Carol Davis", "David Wilson", "Eva Martinez"
    };

    /** Story types used in iterations **/
    private static final String[] TYPES = {"feature", "feature", "feature", "feature", "bug", "bug", "chore"};

    /** Estimate values for features **/
    private static final int[] ESTIMATES = {1, 2, 3, 5, 8, 3, 2, 1, 5, 3};

    /** Feature story titles by index mod **/
    private static final String[] FEATURE_TITLES = {
        "Implement user authentication flow",
        "Add dashboard overview widget",
        "Create CSV export functionality",
        "Integrate payment gateway",
        "Build notification system",
        "Design onboarding wizard",
        "Add multi-language support",
        "Implement dark mode theme",
        "Create API rate limiter",
        "Build advanced search filters",
        "Add two-factor authentication",
        "Implement data pagination",
        "Create audit log viewer",
        "Add team collaboration tools",
        "Build automated report scheduler",
        "Integrate third-party OAuth",
        "Implement role-based access control",
        "Add real-time activity feed",
        "Create mobile-responsive layout",
        "Build data visualization charts"
    };

    /** Bug story titles by index mod **/
    private static final String[] BUG_TITLES = {
        "Fix login redirect loop",
        "Resolve date parsing error in reports",
        "Fix broken pagination on stories list",
        "Correct velocity calculation for chores",
        "Fix CSS alignment in overview page",
        "Resolve NPE when project has no stories",
        "Fix cookie expiration handling",
        "Correct timezone offset in charts",
        "Fix memory leak in data source cache",
        "Resolve encoding issue in story titles"
    };

    /** Chore story titles by index mod **/
    private static final String[] CHORE_TITLES = {
        "Upgrade Maven dependencies",
        "Refactor data source layer",
        "Add logging to API calls",
        "Clean up unused CSS classes",
        "Update server configuration",
        "Migrate to Java 11",
        "Improve build pipeline",
        "Add health check endpoint",
        "Update README documentation",
        "Optimize database queries"
    };

    /** Labels used on features **/
    private static final String[] EXTRA_LABELS = {"backend", "frontend", "ux", "api", "security"};

    /**
     * @see HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "";
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        if (pathInfo.endsWith("/iterations")) {
            handleIterations(response, out);
        } else if (pathInfo.endsWith("/memberships")) {
            handleMemberships(out);
        } else if (pathInfo.endsWith("/stories")) {
            handleStories(out);
        } else {
            handleProject(out);
        }
    }

    /**
     * Returns mock project metadata.
     */
    private void handleProject(PrintWriter out) {
        out.print("{");
        out.print("\"id\":99999,");
        out.print("\"name\":\"Demo Analytics Project\",");
        out.print("\"account_id\":100001,");
        out.print("\"start_date\":\"" + PROJECT_START + "\",");
        out.print("\"current_iteration_number\":" + ITERATION_COUNT + ",");
        out.print("\"iteration_length\":2");
        out.print("}");
    }

    /**
     * Returns mock team memberships.
     */
    private void handleMemberships(PrintWriter out) {
        out.print("[");
        for (int i = 0; i < USER_IDS.length; i++) {
            if (i > 0) out.print(",");
            out.print("{\"person\":{");
            out.print("\"id\":" + USER_IDS[i] + ",");
            out.print("\"name\":\"" + USER_NAMES[i] + "\",");
            out.print("\"username\":\"" + USER_NAMES[i].toLowerCase().replace(" ", "") + "\"");
            out.print("}}");
        }
        out.print("]");
    }

    /**
     * Returns mock iterations with stories.
     * Pagination headers are set so the client knows it received everything.
     */
    private void handleIterations(HttpServletResponse response, PrintWriter out) {
        response.setHeader("X-Tracker-Pagination-Total", String.valueOf(ITERATION_COUNT));
        response.setHeader("X-Tracker-Pagination-Limit", "100000");

        out.print("[");
        for (int iter = 1; iter <= ITERATION_COUNT; iter++) {
            if (iter > 1) out.print(",");
            out.print("{");
            out.print("\"number\":" + iter + ",");
            out.print("\"stories\":");
            out.print(buildIterationStories(iter));
            out.print("}");
        }
        out.print("]");
    }

    /**
     * Returns mock icebox (unscheduled) stories.
     */
    private void handleStories(PrintWriter out) {
        out.print("[");
        int baseId = 900000;
        for (int i = 0; i < 20; i++) {
            if (i > 0) out.print(",");
            long id = baseId + i;
            String type = (i % 3 == 0) ? "bug" : "feature";
            String title = (type.equals("bug"))
                ? BUG_TITLES[i % BUG_TITLES.length] + " (icebox)"
                : FEATURE_TITLES[i % FEATURE_TITLES.length] + " (backlog)";
            long requesterId = USER_IDS[i % USER_IDS.length];
            long ownerId = USER_IDS[(i + 1) % USER_IDS.length];
            String createdAt = isoDate(PROJECT_START_MS + (long)(i * 7) * DAY_MS);
            int estimate = (type.equals("feature")) ? ESTIMATES[i % ESTIMATES.length] : 0;

            out.print("{");
            out.print("\"id\":" + id + ",");
            out.print("\"story_type\":\"" + type + "\",");
            out.print("\"url\":\"https://github.com/matheuscodes/pivotal_analytics/issues/" + id + "\",");
            if (estimate > 0) {
                out.print("\"estimate\":" + estimate + ",");
            }
            out.print("\"current_state\":\"unscheduled\",");
            out.print("\"name\":\"" + escapeJson(title) + "\",");
            out.print("\"requested_by_id\":" + requesterId + ",");
            out.print("\"owner_ids\":[" + ownerId + "],");
            out.print("\"created_at\":\"" + createdAt + "\",");
            out.print("\"labels\":[]");
            out.print("}");
        }
        out.print("]");
    }

    /**
     * Builds a JSON array of stories for a given iteration.
     *
     * @param iter the iteration number (1-based).
     * @return JSON string of the stories array.
     */
    private String buildIterationStories(int iter) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        long iterStartMs = PROJECT_START_MS + (long)(iter - 1) * ITERATION_DAYS * DAY_MS;
        boolean isRecent = iter > ITERATION_COUNT - 4;
        boolean isMid = iter > ITERATION_COUNT - 12 && !isRecent;

        int storyIndex = 0;

        // Features: 4-6 per iteration
        int featureCount = 4 + (iter % 3);
        for (int f = 0; f < featureCount; f++) {
            if (storyIndex > 0) sb.append(",");
            long id = (long) iter * 1000 + f;
            String title = FEATURE_TITLES[(iter * 3 + f) % FEATURE_TITLES.length];
            int estimate = ESTIMATES[(iter + f) % ESTIMATES.length];
            long requesterId = USER_IDS[f % USER_IDS.length];
            long ownerId = USER_IDS[(f + 1) % USER_IDS.length];
            long createdMs = iterStartMs + (long) f * DAY_MS;
            int iterLabel = iter;
            String extraLabel = EXTRA_LABELS[(iter + f) % EXTRA_LABELS.length];

            String state;
            String acceptedAt = null;
            if (isRecent) {
                state = (f < 2) ? "accepted" : (f == 2 ? "delivered" : "started");
            } else if (isMid) {
                state = (f < featureCount - 1) ? "accepted" : "finished";
            } else {
                state = "accepted";
            }

            if (state.equals("accepted")) {
                long acceptedMs = createdMs + (3L + (iter + f) % 8) * DAY_MS;
                acceptedAt = isoDate(acceptedMs);
            }

            appendStory(sb, id, "feature", title, estimate, state, requesterId,
                        ownerId, isoDate(createdMs), acceptedAt,
                        buildFeatureLabels(iterLabel, extraLabel));
            storyIndex++;
        }

        // Bugs: 1-2 per iteration
        int bugCount = 1 + (iter % 2);
        for (int b = 0; b < bugCount; b++) {
            if (storyIndex > 0) sb.append(",");
            long id = (long) iter * 1000 + 100 + b;
            String title = BUG_TITLES[(iter + b) % BUG_TITLES.length];
            long requesterId = USER_IDS[(iter + b) % USER_IDS.length];
            long ownerId = USER_IDS[(iter + b + 2) % USER_IDS.length];
            long createdMs = iterStartMs + (long)(b + 1) * DAY_MS;

            String state;
            String acceptedAt = null;
            if (isRecent && b > 0) {
                state = "started";
            } else {
                state = "accepted";
                long acceptedMs = createdMs + (2L + (iter + b) % 5) * DAY_MS;
                acceptedAt = isoDate(acceptedMs);
            }

            appendStory(sb, id, "bug", title, 0, state, requesterId,
                        ownerId, isoDate(createdMs), acceptedAt, "[]");
            storyIndex++;
        }

        // Chore: 1 per iteration
        {
            long id = (long) iter * 1000 + 200;
            String title = CHORE_TITLES[iter % CHORE_TITLES.length];
            long requesterId = USER_IDS[iter % USER_IDS.length];
            long ownerId = USER_IDS[(iter + 3) % USER_IDS.length];
            long createdMs = iterStartMs + 2L * DAY_MS;

            String state = isRecent ? "unstarted" : "accepted";
            String acceptedAt = null;
            if (state.equals("accepted")) {
                long acceptedMs = createdMs + 5L * DAY_MS;
                acceptedAt = isoDate(acceptedMs);
            }

            sb.append(",");
            appendStory(sb, id, "chore", title, 0, state, requesterId,
                        ownerId, isoDate(createdMs), acceptedAt, "[]");
        }

        // Release: once every 4 iterations
        if (iter % 4 == 0) {
            long id = (long) iter * 1000 + 300;
            String title = "Release v" + (iter / 4) + ".0";
            long requesterId = USER_IDS[0];
            long ownerId = USER_IDS[0];
            long createdMs = iterStartMs + 10L * DAY_MS;

            String state = isRecent ? "unstarted" : "accepted";
            String acceptedAt = null;
            if (state.equals("accepted")) {
                long acceptedMs = createdMs + 3L * DAY_MS;
                acceptedAt = isoDate(acceptedMs);
            }

            sb.append(",");
            appendStory(sb, id, "release", title, 0, state, requesterId,
                        ownerId, isoDate(createdMs), acceptedAt, "[]");
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Appends a story JSON object to a StringBuilder.
     */
    private void appendStory(StringBuilder sb, long id, String type, String title,
            int estimate, String state, long requestedById, long ownerId,
            String createdAt, String acceptedAt, String labelsJson) {
        sb.append("{");
        sb.append("\"id\":").append(id).append(",");
        sb.append("\"story_type\":\"").append(type).append("\",");
        sb.append("\"url\":\"https://github.com/matheuscodes/pivotal_analytics/issues/").append(id).append("\",");
        if (estimate > 0) {
            sb.append("\"estimate\":").append(estimate).append(",");
        }
        sb.append("\"current_state\":\"").append(state).append("\",");
        sb.append("\"name\":\"").append(escapeJson(title)).append("\",");
        sb.append("\"requested_by_id\":").append(requestedById).append(",");
        sb.append("\"owner_ids\":[").append(ownerId).append("],");
        sb.append("\"created_at\":\"").append(createdAt).append("\"");
        if (acceptedAt != null) {
            sb.append(",\"accepted_at\":\"").append(acceptedAt).append("\"");
        }
        sb.append(",\"labels\":").append(labelsJson);
        sb.append("}");
    }

    /**
     * Builds the labels JSON array for a feature story.
     *
     * @param iterLabel the iteration number label (e.g. "[5]").
     * @param extraLabel an additional label string.
     * @return JSON array string.
     */
    private String buildFeatureLabels(int iterLabel, String extraLabel) {
        return "[{\"id\":" + (iterLabel * 10) + ",\"name\":\"[" + iterLabel + "]\"}"
            + ",{\"id\":" + (iterLabel * 10 + 1) + ",\"name\":\"" + escapeJson(extraLabel) + "\"}]";
    }

    /**
     * Formats an epoch millisecond value as an ISO 8601 UTC timestamp string.
     *
     * @param epochMs epoch time in milliseconds.
     * @return formatted date string.
     */
    private String isoDate(long epochMs) {
        synchronized (ISO_FMT) {
            return ISO_FMT.format(new Date(epochMs));
        }
    }

    /**
     * Escapes a string value for safe inclusion in a JSON string literal.
     *
     * @param value the raw string value.
     * @return the escaped string.
     */
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
