import Ember from 'ember';
import sortBy from 'ember-computed-sortby';

export default Ember.Controller.extend({
  store: Ember.inject.service(),

  photos: null,
  sortedPhotos: sortBy('photo', 'createdOn'),

  uploadingCount: 0,
  isUploading: Ember.computed.gt('uploadingCount', 0),

  loadPhotos() {
    let albumId = this.get('album.id');

    this.set('showEditAlbumModal', false);
    this.set('selectedPhoto', null);
    this.set('photos', null);

    this.set('isLoadingPhotos', true);
    this.get('store').query('photo', { album: albumId }).then(photos => {
      this.set('photos', photos.toArray());
    }).finally(() => {
      this.set('isLoadingPhotos', false);
    });
  },

  actions: {
    editAlbum() {
      this.set('showEditAlbumModal', true);
    },

    hideEditAlbum() {
      this.get('album').rollbackAttributes();
      this.set('showEditAlbumModal', false);
    },

    saveAlbum() {
      this.set('isSavingAlbum', true);
      this.get('album').save().then(() => {
        this.set('showEditAlbumModal', false);
      }).finally(() => {
        this.set('isSavingAlbum', false);
      });
    },

    deleteAlbum() {
      let confirmed = window.confirm('Are you sure?');
      if (confirmed) {
        this.set('isSavingAlbum', true);
        this.get('album').destroyRecord().then(() => {
          this.transitionToRoute('albums');
        }).finally(() => {
          this.set('isSavingAlbum', false);
        });
      }
    },

    selectPhoto(photo) {
      this.set('selectedPhoto', photo);
    },

    droppedFiles(fileUploads) {
      let album = this.get('album');
      fileUploads.forEach(fileUpload => {
        this.incrementProperty('uploadingCount');
        album.uploadPhoto(fileUpload).then(newPhoto => {
          let photos = this.get('photos');
          photos.insertAt(0, newPhoto);
        }).finally(() => {
          this.decrementProperty('uploadingCount');
        });
      });
    }
  }
});
