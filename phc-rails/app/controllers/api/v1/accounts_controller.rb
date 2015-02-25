class Api::V1::AccountsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token

  def search
    auth_token = request.headers["HTTP_AUTH_TOKEN"]
    user_id = request.headers["HTTP_USER_ID"]
    if auth_token && user_id
      if user_authenticated?(user_id, auth_token)
        first_name = request.headers["HTTP_FIRSTNAME"]
        last_name = request.headers["HTTP_LASTNAME"]
        result = PersonAccount.fuzzy_search({first_name: first_name}, false).fuzzy_search({last_name: last_name}, false)
        respond_with result
      end
    else
      respond_with "Error: Please include authentication token.", status: 401
    end
  end

  def create
    auth_token = request.headers["HTTP_AUTH_TOKEN"]
    user_id = request.headers["HTTP_USER_ID"]
    if auth_token && user_id
      if user_authenticated?(user_id, auth_token)
        person = PersonAccount.new
        person.first_name = params[:first_name]
        person.last_name = params[:last_name]
        person.sf_id = request.headers[:salesforceid]
        person.save
        respond_with "Successfully saved user!", status: 200, :location => root_url

        ##### TODO: POST TO SALESFORCE #####
      end
    else
      respond_with "Error: Please include authentication token.", status: 401, location => root_ur
    end

  end

  private

    def user_authenticated?(id, token)
      user = User.find_by(id: id)
      if not user
        respond_with "Error: User ID not found in database.", status: 401
        false
      elsif not user.authenticated?(token)
        respond_with "Error: Invalid authentication token.", status: 401
        false
      else
        true
      end
    end

end
