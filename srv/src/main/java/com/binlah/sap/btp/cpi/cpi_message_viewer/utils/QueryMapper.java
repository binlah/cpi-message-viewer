package com.binlah.sap.btp.cpi.cpi_message_viewer.utils;

import java.util.Map;

import com.sap.cds.services.cds.CdsReadEventContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Minimal and robust mapper for OData system query options.
 * This version intentionally avoids CAP CQN internals (CqnSelect, CqnColumn,
 * ...),
 * because those types/methods differ between cds4j versions.
 *
 * We just read the canonical OData params from the HTTP request:
 * $top, $skip, $select, $filter, $expand, $orderby, $search
 *
 * Fiori Elements always sends them explicitly, so this is sufficient for the
 * bridge.
 */
public class QueryMapper {

    public static QuerySpec from(CdsReadEventContext ctx, HttpServletRequest req) {
        QuerySpec spec = new QuerySpec();

        if (req == null) {
            // Nothing we can do without the servlet request; return empty spec
            return spec;
        }

        Map<String, String[]> params = req.getParameterMap();

        // $top / $skip
        Integer top = parseInt(params.get("$top"));
        Integer skip = parseInt(params.get("$skip"));
        if (top != null)
            spec.setTop(top);
        if (skip != null)
            spec.setSkip(skip);

        // $select
        if (params.containsKey("$select")) {
            String rawSel = join(params.get("$select"));
            // keep the raw for passthrough
            spec.putRaw("$select", rawSel);
            // also split into individual props (top-level only) so callers can choose to
            // use typed VDM later
            for (String s : rawSel.split(",")) {
                String p = s.trim();
                if (!p.isEmpty() && !p.contains("/")) { // avoid nested paths here, keep raw for those
                    spec.addSelect(p);
                }
            }
        }

        // $filter / $expand / $orderby / $search
        putPassthrough(spec, params, "$filter");
        putPassthrough(spec, params, "$expand");
        putPassthrough(spec, params, "$orderby");
        putPassthrough(spec, params, "$search");
        // Custom CPI query parameter
        putPassthrough(spec, params, "filterCustomHeaderProperties");

        // Also mirror $filter/$expand into the dedicated fields for convenience
        if (params.containsKey("$filter"))
            spec.setFilterRaw(join(params.get("$filter")));
        if (params.containsKey("$expand"))
            spec.setExpandRaw(join(params.get("$expand")));

        return spec;
    }

    // ---------- helpers ----------

    private static void putPassthrough(QuerySpec spec, Map<String, String[]> params, String key) {
        if (params.containsKey(key)) {
            spec.putRaw(key, join(params.get(key)));
        }
    }

    private static String join(String[] arr) {
        if (arr == null || arr.length == 0)
            return "";
        if (arr.length == 1)
            return arr[0];
        return String.join(",", arr);
    }

    private static Integer parseInt(String[] arr) {
        if (arr == null || arr.length == 0)
            return null;
        try {
            return Integer.parseInt(arr[0]);
        } catch (Exception e) {
            return null;
        }
    }
}
