export interface Picture {
  id: number;
  owner: string;
  url: string;
  content: string;
  length: number;
  mimeType: string | null;
  createDate: string | null;  
}

