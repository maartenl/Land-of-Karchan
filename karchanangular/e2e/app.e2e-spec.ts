import { KarchanangularPage } from './app.po';

describe('karchanangular App', () => {
  let page: KarchanangularPage;

  beforeEach(() => {
    page = new KarchanangularPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
