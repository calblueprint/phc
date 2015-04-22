class AccountsController < ApplicationController
  respond_to :json
  skip_before_action :verify_authenticity_token
#  before_action :verify_security_token

  def duplicates
    # TEMP AUTH TOKEN FOR DEMOS
    auth_token = request.params[:auth_token]
    if auth_token != ENV['AUTH_TOKEN']
      @accounts = []
      return
    end
    attrs = request.params[:attributes]
    attrs = attrs.nil? ? [] : ActiveSupport::JSON.decode(attrs)

    # Account.all(:conditions => { :created_at => (Time.now.midnight - 1.day)..Time.now.midnight})
    @accounts = Account.find_duplicates_by(attrs)
  end
end
