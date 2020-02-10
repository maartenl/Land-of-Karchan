
export class ChristmasUtils {
    public static isChristmas(currentDate: Date = new Date()): boolean {
        // isChristmas(currentDate: Date = new Date(2019, 11, 27)): boolean {
        const beforeChristmas = new Date(currentDate.getFullYear(), 11, 7).getTime();
        const afterChristmas = new Date(currentDate.getFullYear(), 0, 6).getTime();
        return beforeChristmas < currentDate.getTime() || afterChristmas > currentDate.getTime();
    }
}
