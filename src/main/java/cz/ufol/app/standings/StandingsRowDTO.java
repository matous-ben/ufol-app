package cz.ufol.app.standings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StandingsRowDTO {
    private String nazevTymu;
    private Long tymId;
    private int odehrane;
    private int vyhry;
    private int remizy;
    private int prohry;
    private int vstreleneGoly;
    private int obdrzeneGoly;
    private int body;
    private String logoFile;

    public int getGoloveSkore() {
        return vstreleneGoly - obdrzeneGoly;
    }

    public String getSkore() {
        return vstreleneGoly + ":" + obdrzeneGoly;
    }
}
