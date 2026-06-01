package cz.ufol.app.player;

public record PlayerStatView (
        Registration registration,
        long odehraneZapasy,
        long goly
) {
}

