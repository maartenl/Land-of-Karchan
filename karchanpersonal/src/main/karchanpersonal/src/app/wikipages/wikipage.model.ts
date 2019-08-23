export interface Wikipage {
  name: string;
  title: string;
  createDate: string;
  modifiedDate: string;
  version: string;
  content: string;
  summary: string;
  parentTitle: string;
  administration: boolean;
  comment: string;
  ordering: number;
}
