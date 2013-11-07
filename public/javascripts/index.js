$(function(){
    var welcome =$('#welcome'), main = $('#main');

    welcome.find('a').on('click', function(e){
        e.preventDefault();
        welcome.hide();
        main.removeClass('hidden');
        main.find('a:first').tab('show') ;
    })

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        var a = $(e.target), tabContainer = $(a.attr('href'));

        var catalog = a.attr('coursera');
        tabContainer.append('<p><h4>Please wait...</h4></p>')

        $.ajax({
            url: '/coursera/' + encodeURI(catalog),
            dataType: 'json'
        })
        .done(function(){
            tabContainer.empty();
        })
        .error(function(jqXHR, status, error){
            alertError(error);
        })
        .success(function(data){
                var ul = $('<ul>').addClass('list-group');
                $.each(data, function(index, value){
                   ul.append(
                       '<li class="list-group-item">'+
                            '<div class="media">'+
                                '<a class="pull-left" href="#">' +
                                    '<img class="media-object" src="'+ value.image +'" alt="'+ value.name +'">' +
                                '</a>' +
                                '<div class="media-body">' +
                                    '<h4 class="media-heading">'+ value.name +'</h4>' +
                                    '<p>'+ value.university +'</p>' +
                                    '<p>' +
                                        '<a href="'+ value.uri +'" target="_blank" class="btn btn-primary">Show</a>&nbsp;&nbsp;' +
                                        '<a href="#" onclick="showCommentsModal('+ value.topicId +');return false;" target="_blank" class="btn btn-default"><span class="glyphicon glyphicon-comment"></span> Comments</a>' +
                                    '</p>' +
                                '</div>' +
                            '</div>' +
                       '</li>'
                    );
                });

                tabContainer.append(ul);
            });
    })

    var commentsModal = $('#commentsModal'),
        commentForm = commentsModal.find('#commentForm');

    commentsModal.on('show.bs.modal', function(){
        var commentsContainer = commentsModal.find('.list-group');
        commentsContainer.empty().append('<li class="list-group-item">Please wait...</li>');
        $.get('/comment/'+$('#topicId').val())
            .done(function(){
                commentsContainer.empty();
            })
            .error(function(jqXHR, status, error){
                alertError(error);
            })
            .success(function(data) {
                $.each(data, function(i, v){
                    commentsContainer.append(
                        '<li class="list-group-item">'+
                            '<p>'+ v.email +'</p>' +
                            '<p>'+ v.message +'</p>' +
                        '</li>'
                    );
                });

            });
    })

    commentForm.on('submit', function(e){
        e.preventDefault();
        $.post(commentForm.attr('action'), commentForm.serialize());
        commentsModal.modal('toggle');
        commentForm.find('input').val('');

        alertSuccess('New comment has been added')
    })

})

function showCommentsModal(topicId) {
    $('#topicId').val(topicId);
    $('#commentsModal').modal();
}

function alertError(error) {
    alert('alert-error', error);
}

function alertSuccess(msg) {
    alert('alert-success', msg);
}

function alert(cls, message) {
    $('#alert_placeholder').html(
        '<div class="alert ' + cls + ' alert-dismissable">' +
            '<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>' +
            '<span class="alert-message">' + message + '</span>' +
        '</div>');
}
