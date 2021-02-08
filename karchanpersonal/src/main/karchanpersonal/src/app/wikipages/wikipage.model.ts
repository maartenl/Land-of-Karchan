export interface Wikipage {
  name: string;
  title: string;
  createDate: string | null;
  modifiedDate: string | null;
  version: string;
  content: string;
  summary: string;
  parentTitle: string;
  administration: boolean;
  comment: string;
  ordering: number;
}
