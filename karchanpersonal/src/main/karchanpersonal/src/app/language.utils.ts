import { Item } from './play/display.model';

export class LanguageUtils {
    public static isVowel(aChar: string) {
        return aChar === 'a' ||
            aChar === 'e' ||
            aChar === 'u' ||
            aChar === 'o' ||
            aChar === 'i' ||
            aChar === 'A' ||
            aChar === 'E' ||
            aChar === 'U' ||
            aChar === 'I' ||
            aChar === 'O';
    }

    public static getDescription(item: Item) {
        let description = '';
        let i = 0;
        //  A massive, gray, stone boulder is here.
        if (item.adject1 !== undefined && item.adject1 !== '') {
            i++;
            description += item.adject1;
        }
        if (item.adject2 !== undefined && item.adject2 !== '') {
            if (i === 1) {
                description += ', ';
            }
            i++;
            description += item.adject2;
        }
        if (item.adject3 !== undefined && item.adject3 !== '') {
            if (i > 0) {
                description += ', ';
            }
            i++;
            description += item.adject3;
        }
        description += ' ' + item.name;
        if (item.amount > 1) {
            description = item.amount + ' ' + description;
        } else {
            if (this.isVowel(description.charAt(0))) {
                description = 'An ' + description;
            } else {
                description = 'A ' + description;
            }
        }
        return description;
    }

}