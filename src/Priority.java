public enum Priority {
    LOW("🟢", "Low", "#B8E6B8"),
    MEDIUM("🟡", "Medium", "#FFE4A3"),
    HIGH("🔴", "High", "#FFB8B8");

    private final String emoji;
    private final String label;
    private final String color;

    Priority(String emoji, String label, String color) {
        this.emoji = emoji;
        this.label = label;
        this.color = color;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return emoji + " " + label;
    }
}