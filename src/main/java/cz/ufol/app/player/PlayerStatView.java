package cz.ufol.app.player;

public record HracStatView(
        Registrace registrace,
        long odehraneZapasy,
        long goly
) {
}

