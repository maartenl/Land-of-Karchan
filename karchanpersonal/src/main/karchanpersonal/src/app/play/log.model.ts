
export class Log {
    log: string;
    offset: number;
    size: number;
    totalsize: number;
  
    constructor(object?: any) {
      if (object === undefined) {
        this.offset = 9999999999999;
        this.totalsize = 9999999999999;
        this.log = '';
        this.size = 0;
        return;
      }
      this.offset = object.offset;
      this.log = object.log;
      this.size = object.size;
      this.totalsize = object.totalsize;
    }
  
  }
  