final class Pipeline {
    static long run(Item item) {
        double[] v = Rules.parse(item);
        boolean ok = Rules.checksumOk(item, v);

        // set short circuit if check sum not okay
        if (!ok) return Rules.verdict(item, false, false, 0, 0, null, 0);

        long fp = Rules.fingerprint(item);
        boolean dup = false;
        for (int j = Math.max(0, item.id - Rules.DEDUPE_WINDOW); j < item.id; j++)
            if (Rules.fingerprint(Item.TABLE[j]) == fp) {
                dup = true;
                // set short circuit if duplicate
                return Rules.verdict(item, ok, true, fp, 0, null, 0);
            }


        long mask = Rules.relevance(item, v);
        double[] smooth = Rules.normalize(item, v);
        int clen = Rules.compress(item, smooth);

        return Rules.verdict(item, ok, dup, fp, mask, smooth, clen);
    }
}
