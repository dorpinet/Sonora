const SpotifyWebApi = require('spotify-web-api-node');

class SpotifyService {
  constructor() {
    this.spotifyApi = new SpotifyWebApi({
      clientId: process.env.SPOTIFY_CLIENT_ID,
      clientSecret: process.env.SPOTIFY_CLIENT_SECRET,
    });
    this.tokenExpiresAt = null;
  }


  async ensureClientCredentials() {

    if (!this.tokenExpiresAt || new Date() > this.tokenExpiresAt) {
      const data = await this.spotifyApi.clientCredentialsGrant();
      this.spotifyApi.setAccessToken(data.body.access_token);

      this.tokenExpiresAt = new Date(Date.now() + data.body.expires_in * 1000);
    }
  }


  async searchTracks(query, options = {}) {
    await this.ensureClientCredentials();
    const data = await this.spotifyApi.searchTracks(query, {
      limit: options.limit || 20,
      offset: options.offset || 0,
      market: 'US',
    });
    return data.body;
  }


  async getTrackPreview(trackId) {
    await this.ensureClientCredentials();
    const data = await this.spotifyApi.getTrack(trackId, { market: 'US' });
    return {
      previewUrl: data.body.preview_url,
      name: data.body.name,
      artist: data.body.artists?.[0]?.name,
      cover: data.body.album?.images?.[0]?.url,
    };
  }


  async getAlbum(albumId) {
    await this.ensureClientCredentials();
    const data = await this.spotifyApi.getAlbum(albumId, { market: 'US' });
    return data.body;
  }


  async getArtistTopTracks(artistId) {
    await this.ensureClientCredentials();
    const data = await this.spotifyApi.getArtistTopTracks(artistId, 'US');
    return data.body;
  }
}

module.exports = new SpotifyService();
