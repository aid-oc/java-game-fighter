package scripts.MassFighter.Data;

public enum Food {

        ANCHOVIES("Anchovies", 319, 10),
        CRAYFISH("Crayfish", 13433, 10),
        SHRIMPS("Shrimps", 315, 30),
        SARDINE("Sardine", 325, 40),
        HERRING("Herring", 347, 50),
        TROUT("Trout", 333, 70),
        SALMON("Salmon", 329, 90),
        TUNA("Tuna", 361, 100),
        LOBSTER("Lobster", 379, 120),
        BASS("Bass", 365, 130),
        SWORDFISH("Swordfish", 373, 140),
        MONKFISH("Monkfish", 7946, 160),
        SHARK("Shark", 385, 200),
        SUMMER_PIE("Summer pie", 7218, 220),
        TUNA_POTATO("Tuna potato", 7060, 220),
        ROCKTAIL("Rocktail", 15272, 230),
        SWEETCORN("Sweetcorn", 5986, 50);

        private String name;
        private int	id;
        private int	heal;
        private Food(String name, int id, int heal) {
            this.name = name;
            this.id = id;
            this.heal = heal;
        }
        public String getName() {
            return name;
        }
        public int getId() {
            return id;
        }
        public int getHeal() {
            return heal;
        }
}

