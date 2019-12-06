
export class StringUtils {

    private static getImage(character: string): string {
        return '<img alt=\"' +
        character +
        '"\' src=\"/images/gif/letters/' +
        character.toLowerCase() +
        '.gif\" style=\"float: left;\">';
    }

    public static getCapitalized(content: string): string {
        let inTag = false;
        let pos = 0;
        while (pos < content.length) {
            if (content.charAt(pos) === '<') {
                inTag = true;
            }
            if (content.charAt(pos) === '>') {
                inTag = false;
            }
            if (!inTag && content.charAt(pos) >= 'A' && content.charAt(pos) <= 'Z') {
                const charAt = content.charAt(pos);
                return content.substring(0, pos) + this.getImage(charAt) +
                content.substring(pos + 1);
            }
            if (!inTag && content.charAt(pos) >= 'a' && content.charAt(pos) <= 'z') {
                const charAt = content.charAt(pos);
                return content.substring(0, pos) + this.getImage(charAt) +
                content.substring(pos + 1);
            }
            pos++;
        }
        return content;
    }
}